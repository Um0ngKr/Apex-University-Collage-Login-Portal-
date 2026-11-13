<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Payment Management • Apex University" />
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
                    <h2 class="h3 fw-bold mb-1">Fee Payment Transactions &amp; Audit</h2>
                    <p class="text-secondary small mb-0">Reconcile examination fee payments, verify sandbox and gateway transactions, and issue receipts.</p>
                </div>
                <a href="${pageContext.request.contextPath}/export/csv?type=payments&status=${selectedStatus}" class="btn btn-outline-success btn-sm">
                    <i class="bi bi-file-earmark-spreadsheet me-1"></i> Export Transactions CSV
                </a>
            </div>

            <!-- Stats Bar -->
            <div class="row g-3 mb-4">
                <div class="col-md-3 col-6">
                    <div class="p-3 bg-light rounded-4 border text-center">
                        <small class="text-muted fw-bold text-uppercase d-block">Total Collections</small>
                        <span class="fs-4 fw-bolder text-primary">₹${totalSuccessfulAmount}</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="p-3 bg-light rounded-4 border text-center">
                        <small class="text-muted fw-bold text-uppercase d-block">Successful Payments</small>
                        <span class="fs-4 fw-bolder text-success">${successfulCount}</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="p-3 bg-light rounded-4 border text-center">
                        <small class="text-muted fw-bold text-uppercase d-block">Pending Clearance</small>
                        <span class="fs-4 fw-bolder text-warning">${pendingCount}</span>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="p-3 bg-light rounded-4 border text-center">
                        <small class="text-muted fw-bold text-uppercase d-block">Failed / Declined</small>
                        <span class="fs-4 fw-bolder text-danger">${failedCount}</span>
                    </div>
                </div>
            </div>

            <!-- Filter Buttons -->
            <div class="d-flex gap-2 mb-3">
                <a href="${pageContext.request.contextPath}/admin/payments" class="btn btn-sm ${empty selectedStatus ? 'btn-primary' : 'btn-outline-secondary'}">All</a>
                <a href="${pageContext.request.contextPath}/admin/payments?status=SUCCESS" class="btn btn-sm ${selectedStatus == 'SUCCESS' ? 'btn-success' : 'btn-outline-success'}">Successful</a>
                <a href="${pageContext.request.contextPath}/admin/payments?status=PENDING" class="btn btn-sm ${selectedStatus == 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}">Pending</a>
                <a href="${pageContext.request.contextPath}/admin/payments?status=FAILED" class="btn btn-sm ${selectedStatus == 'FAILED' ? 'btn-danger' : 'btn-outline-danger'}">Failed</a>
            </div>

            <!-- Payments Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                        <thead>
                            <tr>
                                <th>Receipt No</th>
                                <th>Transaction ID</th>
                                <th>Student Details</th>
                                <th>Purpose / Exam</th>
                                <th>Amount</th>
                                <th>Mode</th>
                                <th>Date &amp; Time</th>
                                <th>Status</th>
                                <th class="text-center text-nowrap" style="width: 130px; min-width: 130px;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${payments}">
                                <tr>
                                    <td class="fw-bold text-primary">${p.receiptNo}</td>
                                    <td><small class="text-muted text-break">${p.transactionId}</small></td>
                                    <td>
                                        <div class="fw-bold">${p.studentName}</div>
                                        <small class="text-muted">${p.rollNo}</small>
                                    </td>
                                    <td><small class="fw-semibold">${p.examName != null ? p.examName : p.remarks}</small></td>
                                    <td class="fw-bold">₹${p.amount}</td>
                                    <td><span class="badge bg-light text-dark border">${p.paymentMethod}</span></td>
                                    <td><small class="text-muted">${p.paymentDate}</small></td>
                                    <td>
                                        <span class="badge ${p.paymentStatus == 'SUCCESS' ? 'bg-success' : (p.paymentStatus == 'PENDING' ? 'bg-warning' : 'bg-danger')}">
                                            ${p.paymentStatus}
                                        </span>
                                    </td>
                                    <td class="text-center text-nowrap">
                                        <div class="table-actions">
                                            <a href="${pageContext.request.contextPath}/export/pdf?type=receipt&receipt_no=${p.receiptNo}" class="btn btn-sm btn-outline-primary" title="PDF Receipt">
                                                <i class="bi bi-file-earmark-pdf"></i> Receipt
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/payment-action" method="POST" class="d-inline">
                                                <input type="hidden" name="action" value="verify">
                                                <input type="hidden" name="payment_id" value="${p.id}">
                                                <input type="hidden" name="status" value="${p.paymentStatus == 'SUCCESS' ? 'FAILED' : 'SUCCESS'}">
                                                <button type="submit" class="btn btn-sm btn-outline-secondary btn-icon-only" title="Toggle Status (${p.paymentStatus == 'SUCCESS' ? 'Mark FAILED' : 'Mark SUCCESS'})">
                                                    <i class="bi bi-arrow-repeat"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty payments}">
                                <tr><td colspan="9" class="text-center py-5 text-muted">No fee transaction logs found.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
