<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Public Examination Result Search • Apex University" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-7 col-md-9 col-12">
            
            <div class="text-center mb-4">
                <span class="badge bg-warning text-dark px-3 py-2 text-uppercase mb-2">
                    <i class="bi bi-award-fill me-1"></i> Public Result Portal
                </span>
                <h2 class="fw-bolder">Apex University Examination Results</h2>
                <p class="text-secondary small">Enter your University Registration Number or Examination Roll Number to verify and access your certified semester results.</p>
            </div>

            <c:if test="${not empty searchError}">
                <div class="alert alert-warning alert-dismissible fade show d-flex align-items-center gap-2 mb-4" role="alert">
                    <i class="bi bi-exclamation-circle-fill fs-5"></i>
                    <div>${searchError}</div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="card shadow-sm border rounded-4 p-4 mb-4">
                <form action="${pageContext.request.contextPath}/result-search" method="POST">
                    
                    <div class="row g-3">
                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Registration Number <span class="text-muted">(Optional if Roll No provided)</span></label>
                            <input type="text" name="reg_no" class="form-control" value="${searchedReg}" placeholder="e.g. APEX202200101">
                        </div>

                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Examination Roll Number <span class="text-danger">*</span></label>
                            <input type="text" name="roll_no" class="form-control" value="${searchedRoll}" placeholder="e.g. 11500122001" required>
                        </div>

                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Select Examination Session</label>
                            <select name="exam_id" class="form-select">
                                <option value="">-- All Declared Examinations --</option>
                                <c:forEach var="ex" items="${exams}">
                                    <option value="${ex.id}">${ex.examName} (${ex.examCode})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6 col-12">
                            <label class="form-label fw-semibold">Semester Number</label>
                            <select name="semester" class="form-select">
                                <option value="">-- All Semesters --</option>
                                <option value="1">Semester 1</option>
                                <option value="2">Semester 2</option>
                                <option value="3">Semester 3</option>
                                <option value="4">Semester 4</option>
                                <option value="5">Semester 5</option>
                                <option value="6">Semester 6</option>
                                <option value="7">Semester 7</option>
                                <option value="8">Semester 8</option>
                            </select>
                        </div>
                    </div>

                    <div class="mt-4">
                        <button type="submit" class="btn btn-apex-primary w-100 py-2 fs-6">
                            <i class="bi bi-search me-1"></i> Retrieve Official Result &amp; Marksheet
                        </button>
                    </div>

                </form>
            </div>

            <!-- Integrity & Verification Notice -->
            <div class="alert alert-secondary small d-flex align-items-center gap-3">
                <i class="bi bi-shield-check fs-2 text-primary"></i>
                <div>
                    <strong>Confidentiality Notice:</strong> In compliance with Apex University Examination Regulations, only officially declared results are published. Results under review, pending scrutiny, or subject to institutional clearance will be made accessible once approved by the Controller of Examinations.
                </div>
            </div>

        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
