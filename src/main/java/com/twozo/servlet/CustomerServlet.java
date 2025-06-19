package com.twozo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twozo.controller.impl.TirunelveliBankController;
import com.twozo.exception.customer.CustomerProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twozo.controller.BankController;
import com.twozo.controller.impl.TenkasiBankController;
import com.twozo.model.Customer;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		OBJECT_MAPPER.registerModule(new JavaTimeModule());
	}
	private static BankController BANK_CONTROLLER = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServlet.class);


	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final String branchType = request.getParameter("branchType");

		if (branchType == null || branchType.trim().isEmpty()) {
			System.out.println("Branch type is empty or null!");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Branch type is required");
			return;
		}
		if (branchType.equalsIgnoreCase("tenkasi")) {
			BANK_CONTROLLER = TenkasiBankController.getInstance();
		} else if (branchType.equalsIgnoreCase("tirunelveli")) {
			BANK_CONTROLLER = TirunelveliBankController.getInstance();
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid branch type");
			return;
		}
		if ("PATCH".equalsIgnoreCase(request.getMethod())) {
			handlePatch(request, response);
		} else {
			super.service(request, response);
		}
	}
	
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		response.setContentType("application/json");

		try {
			request.setCharacterEncoding("UTF-8");
			final String accountNumber = request.getParameter("accountNumber");

			if (accountNumber == null || accountNumber.trim().isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Account Number is required");
				return;
			}

			if (!BANK_CONTROLLER.isAccountAvailableInBranch(accountNumber)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						"Account number does not exist in this branch");
				return;
			}
			final Customer customer = BANK_CONTROLLER.getCustomerDetails(accountNumber);

			if (customer != null) {
				sendSuccessResponse(response, customer);
			} else {
				sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
			}
		} catch (IOException | CustomerProcessingException e) {
			LOGGER.error("Error writing response: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response : " + e.getMessage());
		} catch (Exception e) {	
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred : " + e.getMessage());
		}
	}

	private void handlePatch(final HttpServletRequest request, final HttpServletResponse response) {
		response.setContentType("application/json");

		try {
			request.setCharacterEncoding("UTF-8");
			final String accountNumber = request.getParameter("accountNumber");

			if (accountNumber == null || accountNumber.trim().isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Account Number is required");
				return;
			}

			if (!BANK_CONTROLLER.isAccountAvailableInBranch(accountNumber)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,"Account number does not exist in this branch");
				return;
			}
			final String requestBody = getRequestBody(request);

			if (requestBody.isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
				return;
			}
			final Map<String, Object> updates = OBJECT_MAPPER.readValue(requestBody, new TypeReference<>() {});

			boolean updateSuccess = false;
			final String updateData = updates.get("updateData").toString();
		    final String updateType = updates.get("updateType").toString();
		    
		    if (updateData == null || updateType == null || updateData.isEmpty() || updateType.isEmpty()) {	
		    	sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid inputs");
			}
		    updateSuccess = BANK_CONTROLLER.updateCustomerDetail(updateData, accountNumber, updateType);
		    
			if (updateSuccess) {
				sendSuccessResponse(response, "Account updated successfully");
			} else {
				LOGGER.error("Failed to update customer {}", accountNumber);
				sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update account");
			}
		} catch (IOException | CustomerProcessingException e) {
			LOGGER.error("Error reading request data", e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading request data : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Invalid JSON format", e);
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format : " + e.getMessage());
		}
	}	

	private String getRequestBody(final HttpServletRequest request) throws IOException {
		final StringBuilder jsonBuffer = new StringBuilder();
		String line;

		try (BufferedReader reader = request.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonBuffer.append(line);
			}
		}
		return jsonBuffer.toString().trim();
	}

	private void sendSuccessResponse(final HttpServletResponse response, final Object responseObject) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(OBJECT_MAPPER.writeValueAsString(responseObject));
	}

	private void sendErrorResponse(final HttpServletResponse response, final int statusCode,
			final String errorMessage) {
		response.setStatus(statusCode);

		try {
			response.getWriter().write(String.format("{\"error\": \"%s\"}", errorMessage));
		} catch (IOException e) {
			LOGGER.error("Error writing error response: {}", e.getMessage(), e);
		}
	}
}
