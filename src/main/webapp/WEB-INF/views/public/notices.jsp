<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Official Notices & Circulars • Apex University" />
</jsp:include>

<div class="container py-5">
    
    <div class="text-center mb-5">
        <span class="badge bg-danger text-white px-3 py-2 text-uppercase mb-2">
            <i class="bi bi-bell-fill me-1"></i> University Bulletins
        </span>
        <h2 class="display-6 fw-bold">Official Notices &amp; Announcements</h2>
        <p class="text-secondary mx-auto" style="max-width: 700px;">
            Stay updated with semester examination schedules, fee deadlines, post-publication scrutiny circulars, and university notifications.
        </p>
    </div>

    <div class="row g-4 justify-content-center">
        <div class="col-lg-10 col-12">
            
            <c:choose>
                <c:when test="${not empty notices}">
                    <c:forEach var="notice" items="${notices}">
                        <div class="card shadow-sm border rounded-4 mb-3 p-4">
                            <div class="d-flex align-items-center justify-content-between mb-2 flex-wrap gap-2">
                                <div class="d-flex align-items-center gap-2">
                                    <span class="badge bg-danger">${notice.category}</span>
                                    <span class="badge bg-secondary">Audience: ${notice.targetAudience}</span>
                                </div>
                                <small class="text-muted"><i class="bi bi-calendar3 me-1"></i> Published: ${notice.publishedAt}</small>
                            </div>
                            <h4 class="h5 fw-bold mb-2 text-primary">${notice.title}</h4>
                            <p class="text-secondary mb-3">${notice.description}</p>
                            <c:if test="${not empty notice.attachmentUrl}">
                                <div>
                                    <a href="${notice.attachmentUrl}" target="_blank" class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-file-earmark-arrow-down me-1"></i> Download Notice Document
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info text-center py-4">
                        <i class="bi bi-info-circle fs-3 d-block mb-2"></i>
                        No public circulars published currently.
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
