package com.twozo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twozo.controller.impl.TenkasiBankController;
import com.twozo.controller.impl.TirunelveliBankController;
import com.twozo.enums.AccountType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twozo.controller.BankController;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.model.Account;
import com.twozo.validation.AccountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/account", "/accounts"})
public class AccountServlet extends HttpServlet {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		OBJECT_MAPPER.registerModule(new JavaTimeModule());
	}
	private static BankController BANK_CONTROLLER = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServlet.class);


	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {
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

			if (accountNumber.trim().isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Account Number is required");
				return;
			}

			if (!BANK_CONTROLLER.isAccountAvailableInBranch(accountNumber)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
						"Account number does not exist in this branch");
				return;
			}
			final Account accountDetails = BANK_CONTROLLER.getAccountDetails(accountNumber);

			if (accountDetails != null) {
				sendSuccessResponse(response, accountDetails);
			} else {
				sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Account not found");
			}
		} catch (IOException | AccountProcessingException e) {
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
			final String requestBody = getRequestBody(request);

			if (requestBody.isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
				return;
			}

			final Account account = OBJECT_MAPPER.readValue(requestBody, Account.class);

			if (!AccountValidator.validationAccount(account)) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid input from user");
				return;
			}

			if (!BANK_CONTROLLER.createAccount(account)) {
				sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create account");
			}
			sendSuccessResponse(response, account);

		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Encoding not supported: {}", e.getMessage(), e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encoding not supported : " + e.getMessage());
		} catch (IOException | AccountProcessingException e) {
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
			final String requestBody = getRequestBody(request);

			if (requestBody.isEmpty()) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Request body is empty");
				return;
			}
			final Map<String, Object> updates = OBJECT_MAPPER.readValue(requestBody, new TypeReference<>() {});

			boolean updateSuccess = false;

			if (updates.containsKey("accountType")) {
				final AccountType accountType = AccountType
						.valueOf(updates.get("accountType").toString().toUpperCase());
				updateSuccess = BANK_CONTROLLER.updateAccountType(accountNumber, accountType);
			}

			if (updates.containsKey("status")) {
				final boolean deactivate = Boolean.parseBoolean(updates.get("status").toString());

				if (!deactivate) {
					updateSuccess = BANK_CONTROLLER.deactivateAccount(accountNumber);
				}
			}

			if (updateSuccess) {
				sendSuccessResponse(response,"Account updated successfully");
			} else {
				LOGGER.error("Failed to update customer {}", accountNumber);
				sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update account");
			}
		} catch (IOException | AccountProcessingException e) {
			LOGGER.error("Error reading request data", e);
			sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading request data : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Invalid JSON format", e);
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format : "  + e.getMessage());
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
