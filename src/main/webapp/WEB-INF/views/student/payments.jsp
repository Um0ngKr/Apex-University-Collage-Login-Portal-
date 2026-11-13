<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Fee Payments & Receipts • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="payments" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Fee Clearance &amp; Electronic Receipts</h2>
                    <p class="text-secondary small mb-0">Clear semester examination fees and retrieve computer-generated verification receipts.</p>
                </div>
            </div>

            <!-- Pending Examination Dues Card -->
            <c:if test="${not empty pendingForms}">
                <div class="card shadow-sm border border-warning rounded-4 p-4 mb-4 bg-warning bg-opacity-10">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold text-dark mb-0">
                            <i class="bi bi-exclamation-triangle-fill text-warning me-2"></i> Pending Examination Fee Dues
                        </h4>
                        <span class="badge bg-warning text-dark">${pendingForms.size()} Pending</span>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered bg-white align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Examination Session</th>
                                    <th>Submission Date</th>
                                    <th>Fee Amount</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="pf" items="${pendingForms}">
                                    <tr>
                                        <td>
                                            <strong>${pf.examName}</strong><br>
                                            <small class="text-muted">${pf.examCode}</small>
                                        </td>
                                        <td>${pf.submissionDate}</td>
                                        <td class="fw-bold fs-6 text-primary">INR ${pf.feeAmount}</td>
                                        <td>
                                            <button type="button" class="btn btn-apex-primary btn-sm"
                                                    onclick="openSandboxPaymentModal(${pf.id}, ${pf.examId}, '${pf.examName}', ${pf.feeAmount})">
                                                <i class="bi bi-credit-card me-1"></i> Pay Now (Sandbox Mode)
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- Payment History & Electronic Receipts -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="card-header bg-transparent border-bottom py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0"><i class="bi bi-receipt text-success me-2"></i> Payment History &amp; Official Receipts</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Receipt Number</th>
                                <th>Transaction ID</th>
                                <th>Examination Title</th>
                                <th>Amount Paid</th>
                                <th>Payment Mode</th>
                                <th>Timestamp</th>
                                <th>Status</th>
                                <th class="text-center">Receipt PDF</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="pay" items="${paymentHistory}">
                                <tr>
                                    <td class="fw-bold text-primary">${pay.receiptNo}</td>
                                    <td><small class="text-muted text-break">${pay.transactionId}</small></td>
                                    <td><small class="fw-semibold">${pay.examName != null ? pay.examName : pay.remarks}</small></td>
                                    <td class="fw-bold">₹${pay.amount}</td>
                                    <td><span class="badge bg-light text-dark border">${pay.paymentMethod}</span></td>
                                    <td><small class="text-muted">${pay.paymentDate}</small></td>
                                    <td>
                                        <span class="badge ${pay.paymentStatus == 'SUCCESS' ? 'bg-success' : 'bg-warning'}">${pay.paymentStatus}</span>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/export/pdf?type=receipt&receipt_no=${pay.receiptNo}" 
                                           class="btn btn-sm btn-outline-primary" title="Download Electronic Receipt">
                                            <i class="bi bi-file-earmark-pdf"></i> Download PDF
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty paymentHistory}">
                                <tr><td colspan="8" class="text-center py-4 text-muted">No fee transaction receipts found.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<!-- Interactive Sandbox Payment Gateway Simulator Modal -->
<div class="modal fade" id="sandboxPaymentModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow-lg overflow-hidden">
            
            <!-- Modal Header with Sandbox Badge -->
            <div class="modal-header bg-primary text-white p-4" style="background: linear-gradient(135deg, #0a192f 0%, #1e3a8a 100%) !important;">
                <div>
                    <div class="badge bg-warning text-dark text-uppercase mb-1"><i class="bi bi-cone-striped me-1"></i> Sandbox Test Gateway</div>
                    <h5 class="modal-title fw-bold mb-0">Apex University Online Payment Gateway</h5>
                </div>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <form action="${pageContext.request.contextPath}/student/pay-fee" method="POST">
                <input type="hidden" name="form_id" id="payFormId">
                <input type="hidden" name="exam_id" id="payExamId">
                <input type="hidden" name="amount" id="payAmount">

                <div class="modal-body p-4">
                    
                    <!-- Sandbox Notice -->
                    <div class="alert alert-info py-2 px-3 small d-flex align-items-center gap-2 mb-3">
                        <i class="bi bi-shield-check fs-4"></i>
                        <div>
                            <strong>Demo Environment:</strong> Transactions simulated in sandbox mode. No real money will be charged from your account.
                        </div>
                    </div>

                    <div class="p-3 bg-light rounded-3 border mb-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="text-muted small">Examination:</span>
                            <span class="fw-bold small text-truncate" id="payExamName">Semester Examination</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                            <span class="fw-bold">Total Payable:</span>
                            <span class="fs-4 fw-bolder text-primary" id="payDisplayAmount">INR 1200.00</span>
                        </div>
                    </div>

                    <!-- Payment Mode Selection -->
                    <label class="form-label fw-semibold small text-uppercase text-muted">Select Payment Method</label>
                    <div class="d-flex flex-column gap-2 mb-3">
                        <div class="form-check p-3 rounded-3 border">
                            <input class="form-check-input" type="radio" name="payment_method" id="modeCard" value="SANDBOX_CARD" checked>
                            <label class="form-check-label fw-semibold d-flex align-items-center justify-content-between w-100" for="modeCard">
                                <span><i class="bi bi-credit-card-2-front me-2 text-primary"></i> Debit / Credit Card Simulation</span>
                                <small class="text-muted">Visa / Master / RuPay</small>
                            </label>
                        </div>

                        <div class="form-check p-3 rounded-3 border">
                            <input class="form-check-input" type="radio" name="payment_method" id="modeUpi" value="SANDBOX_UPI">
                            <label class="form-check-label fw-semibold d-flex align-items-center justify-content-between w-100" for="modeUpi">
                                <span><i class="bi bi-qr-code me-2 text-success"></i> Instant UPI / QR Simulation</span>
                                <small class="text-muted">GPay / PhonePe / Paytm</small>
                            </label>
                        </div>

                        <div class="form-check p-3 rounded-3 border">
                            <input class="form-check-input" type="radio" name="payment_method" id="modeNet" value="SANDBOX_NETBANKING">
                            <label class="form-check-label fw-semibold d-flex align-items-center justify-content-between w-100" for="modeNet">
                                <span><i class="bi bi-bank me-2 text-warning"></i> Internet Banking Simulation</span>
                                <small class="text-muted">All Major Banks</small>
                            </label>
                        </div>
                    </div>

                    <div class="small text-muted text-center">
                        <i class="bi bi-lock-fill text-success"></i> Protected by 256-bit TLS Encryption. Retain generated transaction ID.
                    </div>

                </div>

                <div class="modal-footer p-3 bg-light">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success px-4 fw-bold">
                        <i class="bi bi-check2-circle me-1"></i> Authorize Payment &amp; Issue Receipt
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
