<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Official Examination Instructions • Apex University" />
</jsp:include>

<div class="container py-5">
    
    <div class="text-center mb-5">
        <span class="badge bg-primary text-white px-3 py-2 text-uppercase mb-2">
            <i class="bi bi-journal-check me-1"></i> Examination Standard Operating Procedures
        </span>
        <h2 class="display-6 fw-bold">Official Examination &amp; Academic Instructions</h2>
        <p class="text-secondary mx-auto" style="max-width: 720px;">
            Read through the complete guidelines for semester form fill-up, fee clearance, hall ticket download, result scrutiny, and institutional verification.
        </p>
    </div>

    <!-- Instruction Cards Accordion / List -->
    <div class="row g-4 justify-content-center">
        <div class="col-lg-10 col-12">
            <c:forEach var="inst" items="${instructions}" varStatus="status">
                <div class="card shadow-sm border rounded-4 mb-4 overflow-hidden">
                    <div class="card-header bg-light border-bottom d-flex align-items-center justify-content-between p-3">
                        <div class="d-flex align-items-center gap-2">
                            <span class="badge bg-warning text-dark fs-6">${status.index + 1}</span>
                            <span class="badge bg-primary">${inst.category}</span>
                            <h5 class="fw-bold mb-0 text-dark">${inst.title}</h5>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <div class="text-secondary" style="white-space: pre-line; line-height: 1.7; font-size: 0.95rem;">
                            ${inst.content}
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- Helpdesk link prompt -->
    <div class="text-center mt-4">
        <p class="text-muted">Need further assistance with instructions? <a href="${pageContext.request.contextPath}/helpdesk" class="fw-bold">Contact University HelpDesk</a></p>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
