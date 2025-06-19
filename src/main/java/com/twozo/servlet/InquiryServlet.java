package com.twozo.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twozo.controller.BankController;

import com.twozo.controller.impl.TenkasiBankController;
import com.twozo.controller.impl.TirunelveliBankController;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/inquiry")
public class InquiryServlet extends HttpServlet {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServlet.class);

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("application/json");

        try {
            request.setCharacterEncoding("UTF-8");
            final String branchType = request.getParameter("branchType");

            if (branchType == null || branchType.trim().isEmpty()) {
                System.out.println("Branch type is empty or null!");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Branch type is required");
                return;
            }
            BankController bankController;

            if (branchType.equalsIgnoreCase("tenkasi")) {
                bankController = TenkasiBankController.getInstance();
            } else if (branchType.equalsIgnoreCase("tirunelveli")) {
                bankController = TirunelveliBankController.getInstance();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid branch type");
                return;
            }
            final String accountNumber = request.getParameter("accountNumber");

            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Account Number is required");
                return;
            }

            if (!bankController.isAccountPresent(accountNumber)) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Account number does not exist");
                return;
            }
            final double amount = bankController.enquireBalance(accountNumber);

            if (amount != 0) {
                sendSuccessResponse(response, amount);
            } else {
                LOGGER.error("Failed to get the amount {}", accountNumber);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Account not found");
            }
        } catch (IOException e) {
            LOGGER.error("Error writing response: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred : " + e.getMessage());
        }
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
