<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Security & Audit Logs • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="security" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Security, Audit Trails &amp; Database Governance</h2>
                    <p class="text-secondary small mb-0">Inspect immutable security audit logs, monitor privileged sessions, and verify database integrity.</p>
                </div>
            </div>

            <!-- Database Backup & Health Card -->
            <div class="card shadow-sm border border-primary rounded-4 p-4 mb-4">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                        <h4 class="h5 fw-bold mb-1"><i class="bi bi-database-check text-primary me-2"></i>MySQL Database: <code>apex_university_db</code></h4>
                        <p class="text-muted small mb-0">InnoDB transactional engine with complete referential foreign key constraints and audit logging.</p>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="button" class="btn btn-outline-primary btn-sm" onclick="alert('Database Schema Script (database.sql) is available in the project root directory.')">
                            <i class="bi bi-download me-1"></i> View Schema SQL
                        </button>
                    </div>
                </div>
            </div>

            <!-- Audit Trail Table -->
            <div class="card shadow-sm border rounded-4 overflow-hidden mb-4">
                <div class="card-header bg-transparent border-bottom py-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-shield-shaded text-success me-1"></i> System Activity Audit Log</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 apex-table">
                        <thead>
                            <tr>
                                <th>Timestamp</th>
                                <th>User Role</th>
                                <th>Username</th>
                                <th>Action Performed</th>
                                <th>Table Affected</th>
                                <th>Details</th>
                                <th>Client IP</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="log" items="${auditLogs}">
                                <tr>
                                    <td><small class="text-muted">${log.createdAt}</small></td>
                                    <td><span class="badge bg-secondary">${log.userType}</span></td>
                                    <td class="fw-bold">${log.username}</td>
                                    <td><span class="badge bg-primary">${log.action}</span></td>
                                    <td><code>${log.tableAffected != null ? log.tableAffected : '-'}</code></td>
                                    <td><small>${log.details}</small></td>
                                    <td><small class="text-muted">${log.ipAddress}</small></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty auditLogs}">
                                <tr><td colspan="7" class="text-center py-5 text-muted">No audit trails recorded yet.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
