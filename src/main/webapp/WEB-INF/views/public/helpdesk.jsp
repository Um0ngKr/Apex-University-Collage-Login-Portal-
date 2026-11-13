<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Official HelpDesk & Support • Apex University of Technology" />
</jsp:include>

<div class="container py-5">
    
    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

    <!-- Helpdesk Banner -->
    <div class="text-center mb-5">
        <span class="badge bg-warning text-dark px-3 py-2 text-uppercase mb-2">
            <i class="bi bi-headset me-1"></i> Student &amp; Institutional Support
        </span>
        <h2 class="display-6 fw-bold">University Technical &amp; Examination HelpDesk</h2>
        <p class="text-secondary mx-auto" style="max-width: 700px;">
            Reach dedicated university officers and technical helplines for examination form fill-up, fee clearance, admit card queries, and transcript requests.
        </p>
    </div>

    <!-- Official University Campus Contact Card -->
    <div class="card shadow-sm border-0 bg-primary text-white mb-5 rounded-4 p-4" style="background: linear-gradient(135deg, #0a192f 0%, #1e3a8a 100%) !important;">
        <div class="row align-items-center g-3">
            <div class="col-md-7">
                <h4 class="fw-bold mb-1">Apex University of Technology</h4>
                <p class="text-warning mb-2"><i class="bi bi-geo-alt-fill"></i> Kaikhali Main Rd, Apex University Campus</p>
                <p class="small text-white-50 mb-0">Official University Communications &amp; Examinations Authority</p>
            </div>
            <div class="col-md-5 text-md-end">
                <a href="mailto:umangkr871170@gmail.com" class="btn btn-apex-gold">
                    <i class="bi bi-envelope-fill me-1"></i> umangkr871170@gmail.com
                </a>
            </div>
        </div>
    </div>

    <!-- Contact Categories Grid -->
    <div class="row g-4 mb-5">
        
        <!-- SECTION 1: Online Payment Issues -->
        <div class="col-lg-4 col-md-6 col-12">
            <div class="card h-100 shadow-sm border rounded-4 p-4">
                <div class="d-flex align-items-center gap-3 mb-3">
                    <div class="bg-primary text-white rounded-3 p-3 fs-4">
                        <i class="bi bi-credit-card-2-front"></i>
                    </div>
                    <div>
                        <h5 class="fw-bold mb-0">Online Payment Issues</h5>
                        <small class="text-warning fw-semibold">Fee Verification Desk</small>
                    </div>
                </div>
                <hr class="my-2">
                <ul class="list-unstyled d-flex flex-column gap-3 mt-3 mb-0">
                    <li>
                        <small class="text-muted d-block">Payment Helpline</small>
                        <a href="tel:8709878369" class="fw-bold fs-5 text-primary text-decoration-none">
                            <i class="bi bi-telephone-fill me-1"></i> 8709878369
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">Payment Support Email</small>
                        <a href="mailto:adarshraj112008@gmail.com" class="fw-semibold text-decoration-underline text-break">
                            <i class="bi bi-envelope-fill me-1"></i> adarshraj112008@gmail.com
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">24/7 Customer Care</small>
                        <a href="tel:7667046114" class="fw-bold text-success text-decoration-none">
                            <i class="bi bi-headset me-1"></i> 7667046114 (24/7 Helpline)
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- SECTION 2: Technical Issues -->
        <div class="col-lg-4 col-md-6 col-12">
            <div class="card h-100 shadow-sm border rounded-4 p-4">
                <div class="d-flex align-items-center gap-3 mb-3">
                    <div class="bg-success text-white rounded-3 p-3 fs-4">
                        <i class="bi bi-cpu"></i>
                    </div>
                    <div>
                        <h5 class="fw-bold mb-0">Technical Issues</h5>
                        <small class="text-success fw-semibold">Systems &amp; Portal Cell</small>
                    </div>
                </div>
                <hr class="my-2">
                <ul class="list-unstyled d-flex flex-column gap-3 mt-3 mb-0">
                    <li>
                        <small class="text-muted d-block">Technical Helpdesk</small>
                        <a href="tel:08709878369" class="fw-bold fs-5 text-success text-decoration-none">
                            <i class="bi bi-telephone-fill me-1"></i> 08709878369
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">Examination Automation Support</small>
                        <a href="tel:8709878369" class="fw-bold fs-5 text-primary text-decoration-none">
                            <i class="bi bi-telephone-fill me-1"></i> 8709878369
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">Technical Support Email</small>
                        <a href="mailto:umangkr87170@gmail.com" class="fw-semibold text-decoration-underline text-break">
                            <i class="bi bi-envelope-fill me-1"></i> umangkr87170@gmail.com
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- SECTION 3: Other Queries & Examinations -->
        <div class="col-lg-4 col-md-12 col-12">
            <div class="card h-100 shadow-sm border rounded-4 p-4">
                <div class="d-flex align-items-center gap-3 mb-3">
                    <div class="bg-warning text-dark rounded-3 p-3 fs-4">
                        <i class="bi bi-file-earmark-lock2"></i>
                    </div>
                    <div>
                        <h5 class="fw-bold mb-0">Academic &amp; Certificate Queries</h5>
                        <small class="text-muted fw-semibold">Administrative Sections</small>
                    </div>
                </div>
                <hr class="my-2">
                <ul class="list-unstyled d-flex flex-column gap-3 mt-3 mb-0">
                    <li>
                        <small class="text-muted d-block">Controller of Examinations / Transcripts / Duplicate Documents</small>
                        <a href="tel:9508153596" class="fw-bold text-dark text-decoration-none">
                            <i class="bi bi-telephone-fill me-1 text-primary"></i> 95081 53596
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">Registration &amp; Migration Section</small>
                        <a href="tel:7061425471" class="fw-bold text-dark text-decoration-none">
                            <i class="bi bi-telephone-fill me-1 text-primary"></i> 70614 25471
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">Transcript / Certificate Support</small>
                        <a href="tel:7320991072" class="fw-bold text-dark text-decoration-none">
                            <i class="bi bi-telephone-fill me-1 text-primary"></i> 7320 991 072
                        </a>
                    </li>
                    <li>
                        <small class="text-muted d-block">General Campus Enquiry</small>
                        <a href="tel:7667046114" class="fw-bold text-primary text-decoration-none">
                            <i class="bi bi-telephone-fill me-1"></i> 7667046114
                        </a>
                    </li>
                </ul>
            </div>
        </div>

    </div>

    <!-- Interactive Helpdesk Query Form -->
    <div class="card shadow-sm border rounded-4 p-4 p-md-5">
        <h4 class="fw-bold mb-2">Submit an Online Helpdesk Query</h4>
        <p class="text-secondary small mb-4">Have an unresolved issue with examination form submission, admit card generation, or payment reconciliation? Submit your query directly to our support desk.</p>

        <form action="${pageContext.request.contextPath}/helpdesk" method="POST">
            <div class="row g-3">
                <div class="col-md-6 col-12">
                    <label class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                    <input type="text" name="name" class="form-control" placeholder="Enter your full name" required>
                </div>
                <div class="col-md-6 col-12">
                    <label class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                    <input type="email" name="email" class="form-control" placeholder="name@example.com" required>
                </div>
                <div class="col-md-6 col-12">
                    <label class="form-label fw-semibold">Registration or Roll Number</label>
                    <input type="text" name="reg_no" class="form-control" placeholder="e.g. APEX202200101">
                </div>
                <div class="col-md-6 col-12">
                    <label class="form-label fw-semibold">Issue Category <span class="text-danger">*</span></label>
                    <select name="subject" class="form-select" required>
                        <option value="Online Fee Payment Discrepancy">Online Fee Payment Discrepancy</option>
                        <option value="Examination Form Submission Problem">Examination Form Submission Problem</option>
                        <option value="Admit Card Not Downloadable">Admit Card Not Downloadable</option>
                        <option value="Marksheet & Result Scrutiny">Marksheet &amp; Result Scrutiny</option>
                        <option value="Registration / Migration Certificate">Registration / Migration Certificate</option>
                        <option value="Other Technical Query">Other Technical Query</option>
                    </select>
                </div>
                <div class="col-12">
                    <label class="form-label fw-semibold">Detailed Description of Problem <span class="text-danger">*</span></label>
                    <textarea name="message" class="form-control" rows="4" placeholder="Please mention transaction IDs, dates, subject codes, or specific error messages encountered..." required></textarea>
                </div>
                <div class="col-12 mt-4">
                    <button type="submit" class="btn btn-apex-primary px-4 py-2">
                        <i class="bi bi-send-fill me-1"></i> Submit HelpDesk Ticket
                    </button>
                </div>
            </div>
        </form>
    </div>

</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
