package com.twozo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twozo.controller.impl.TirunelveliBankController;
import com.twozo.model.EducationLoan;
import com.twozo.model.HomeLoan;
import com.twozo.model.PersonalLoan;
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
import com.twozo.model.Loan;

@WebServlet("/loan")
public class LoanServlet extends HttpServlet {

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
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		try {
			request.setCharacterEncoding("UTF-8");
			final String loanNumber = request.getParameter("loanNumber");

			if (loanNumber == null || loanNumber.trim().isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Loan Number is required");
				return;
			}

			if (!BANK_CONTROLLER.isLoanNumberPresent(loanNumber)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Loan number does not exist");
				return;
			}
			final Loan loan = BANK_CONTROLLER.getLoanDetails(loanNumber);

			if (loan != null) {
				sendSuccessResponse(response, loan);
			} else {
				sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Loan not found");
			}
		} catch (IOException e) {
			LOGGER.error("Error writing response: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred : " + e.getMessage());
		}
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		response.setContentType("application/json");

		try {
			request.setCharacterEncoding("UTF-8");
			final String loanType = request.getParameter("loanType");
			final String requestBody = getRequestBody(request);

			if (requestBody.isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
				return;
			}

			Loan loan = null;

			if (loanType.equalsIgnoreCase("homeloan")) {
				loan = OBJECT_MAPPER.readValue(requestBody, HomeLoan.class);
			} else if (loanType.equalsIgnoreCase("personalloan")) {
				loan = OBJECT_MAPPER.readValue(requestBody, PersonalLoan.class);
			} else if(loanType.equalsIgnoreCase("educationloan")) {
				loan = OBJECT_MAPPER.readValue(requestBody, EducationLoan.class);
			}

			if (loan == null) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid input from user");
				return;
			}

			if (!BANK_CONTROLLER.applyloan(loan)) {
				sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create loan");
				return;
			}
			sendSuccessResponse(response, loan);

		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Encoding not supported: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encoding not supported");
		} catch (IOException e) {
			LOGGER.error("Error reading request data: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading request data : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format : " + e.getMessage());
		}
	}
	
	private void handlePatch(final HttpServletRequest request, final HttpServletResponse response) {
		response.setContentType("application/json");

		try {
			request.setCharacterEncoding("UTF-8");
			final String loanNumber = request.getParameter("loanNumber");

			if (loanNumber == null || loanNumber.trim().isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Loan Number is required");
				return;
			}

			if (!BANK_CONTROLLER.isLoanNumberPresent(loanNumber)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Loan Number does not exist");
				return;
			}
			final String requestBody = getRequestBody(request);

			if (requestBody.isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
				return;
			}
			final Map<String, Object> updates = OBJECT_MAPPER.readValue(requestBody, new TypeReference<Map<String, Object>>() {});
			boolean updateSuccess = false;

			if (updates.containsKey("updateLoanDetails")) {
				final String updateData = updates.get("updateData").toString();
				final String updateType = updates.get("updateType").toString();

				if (updateData == null || updateType == null || updateData.isEmpty() || updateType.isEmpty()) {
					sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid inputs");
					return;
				}
				updateSuccess = BANK_CONTROLLER.updateLoanDetails(loanNumber, updateData, updateType);

				if (updateSuccess) {
					sendSuccessResponse(response, "Account updated successfully");
					return;
				} else {
					LOGGER.error("Failed Loan Updation {}", loanNumber);
					sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update account");
					return;
				}
			}

			if (!updates.containsKey("amount")) {
			    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Amount is required");
			    return;
			}
			final double repaymentAmount = Double.parseDouble(updates.get("amount").toString());
			
	        if (updates.containsKey("repayLoanAmount")) {
	        	updateSuccess = BANK_CONTROLLER.repayLoanAmount(loanNumber, repaymentAmount);
	        }	

	        if (updates.containsKey("closeLoan")) {
	        	updateSuccess = BANK_CONTROLLER.closeLoan(loanNumber, repaymentAmount);
	        }

	        if (updateSuccess) {
	            sendSuccessResponse(response, "Account updated successfully");
	        } else {
	        	LOGGER.error("Failed Loan Updation {}", loanNumber);
	            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update account");
	        }
		} catch (IOException e) {
			LOGGER.error("Error reading request data", e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading request data : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Invalid JSON format", e);
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format : " + e.getMessage());
		}
	}

	private String getRequestBody(final HttpServletRequest request) throws IOException {
		StringBuilder jsonBuffer = new StringBuilder();
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
