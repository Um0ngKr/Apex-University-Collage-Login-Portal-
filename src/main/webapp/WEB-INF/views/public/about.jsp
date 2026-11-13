<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="About Us • Apex University of Technology" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-10 col-12">
            
            <div class="card shadow-sm border rounded-4 p-4 p-md-5">
                <div class="text-center pb-4 mb-4 border-bottom">
                    <img src="${pageContext.request.contextPath}/static/images/logo.svg" alt="Apex Logo" style="width: 80px; height: 80px;" class="mb-3">
                    <h2 class="fw-bold text-primary mb-1">Apex University of Technology</h2>
                    <p class="text-warning fw-semibold text-uppercase">Examination &amp; Academic Governance</p>
                </div>

                <h4 class="fw-bold mb-3">About the Examination &amp; Academic Management System</h4>
                <p class="text-secondary" style="line-height: 1.8;">
                    Apex University of Technology (APEX) is committed to excellence, integrity, and innovation in higher education and technology. The Examination and Academic Management System (EAMS) is an enterprise web solution designed to automate end-to-end examination processes across all academic faculties, affiliated colleges, faculty evaluators, and enrolled scholars.
                </p>

                <h5 class="fw-bold mt-4 mb-3">Core Pillars of the Examination Division</h5>
                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded border h-100">
                            <h6 class="fw-bold text-primary"><i class="bi bi-shield-check me-1"></i> Integrity &amp; Transparency</h6>
                            <p class="small text-muted mb-0">Role-based controls, tamper-proof marks calculation, and automated grading compliant with international credit standards.</p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded border h-100">
                            <h6 class="fw-bold text-primary"><i class="bi bi-lightning-charge me-1"></i> Automation &amp; Speed</h6>
                            <p class="small text-muted mb-0">Instant generation of digital admit cards, electronic fee verification, and on-demand semester marksheet downloads.</p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded border h-100">
                            <h6 class="fw-bold text-primary"><i class="bi bi-people me-1"></i> Stakeholder Collaboration</h6>
                            <p class="small text-muted mb-0">Unified workflows connecting Students, Faculty Members, College Principals, and the Central Controller of Examinations.</p>
                        </div>
                    </div>
                </div>

                <div class="p-3 bg-warning bg-opacity-10 border border-warning rounded">
                    <strong>Note:</strong> Apex University of Technology – Examination &amp; Academic Management System operates strictly under standard university charter rules. All documents generated through this portal bear computerized digital validation seals.
                </div>
            </div>

        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
