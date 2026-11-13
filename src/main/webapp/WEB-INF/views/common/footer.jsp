<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String cpFooter = request.getContextPath();
%>
<!-- Professional University Footer -->
<footer class="apex-footer">
    <div class="container">
        <div class="row g-4">
            
            <!-- Column 1: University Identity -->
            <div class="col-lg-4 col-md-6">
                <div class="d-flex align-items-center gap-2 mb-3">
                    <img src="<%= cpFooter %>/static/images/logo.svg" alt="Apex Logo" style="width: 44px; height: 44px;">
                    <div>
                        <h5 class="text-white fw-bold mb-0">APEX UNIVERSITY OF TECHNOLOGY</h5>
                        <small class="text-warning text-uppercase">Examination Division</small>
                    </div>
                </div>
                <p class="text-secondary small mb-3">
                    A premier technological university portal facilitating centralized semester examinations, dynamic student enrollment, admit card issuance, computerized result publication, and secure academic administration.
                </p>
                <div class="d-flex gap-3 text-secondary">
                    <span class="badge bg-secondary"><i class="bi bi-shield-check me-1"></i> ISO 27001 Certified Portal</span>
                    <span class="badge bg-secondary"><i class="bi bi-lock me-1"></i> 256-Bit SSL Encrypted</span>
                </div>
            </div>

            <!-- Column 2: Quick Links & Policies -->
            <div class="col-lg-3 col-md-6">
                <h5 class="footer-heading">Quick Links &amp; Policies</h5>
                <ul class="footer-links">
                    <li><a href="<%= cpFooter %>/"><i class="bi bi-chevron-right me-1 text-warning"></i> Home Portal</a></li>
                    <li><a href="<%= cpFooter %>/about"><i class="bi bi-chevron-right me-1 text-warning"></i> About Us</a></li>
                    <li><a href="<%= cpFooter %>/result-search"><i class="bi bi-chevron-right me-1 text-warning"></i> Result Verification</a></li>
                    <li><a href="<%= cpFooter %>/instructions"><i class="bi bi-chevron-right me-1 text-warning"></i> Examination Instructions</a></li>
                    <li><a href="<%= cpFooter %>/privacy"><i class="bi bi-chevron-right me-1 text-warning"></i> Privacy Policy</a></li>
                    <li><a href="<%= cpFooter %>/refund"><i class="bi bi-chevron-right me-1 text-warning"></i> Refund &amp; Cancellation Policy</a></li>
                    <li><a href="<%= cpFooter %>/terms"><i class="bi bi-chevron-right me-1 text-warning"></i> Terms &amp; Conditions</a></li>
                </ul>
            </div>

            <!-- Column 3: Contact & HelpDesk Information -->
            <div class="col-lg-5 col-md-12">
                <h5 class="footer-heading">Official University Contact</h5>
                <div class="d-flex flex-column gap-2 text-secondary small">
                    <div class="d-flex align-items-start gap-2">
                        <i class="bi bi-geo-alt-fill text-warning fs-5"></i>
                        <span><strong>Campus Address:</strong> Kaikhali Main Rd, Apex University of Technology Campus</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-envelope-fill text-warning fs-5"></i>
                        <span><strong>University Email:</strong> <a href="mailto:umangkr871170@gmail.com" class="text-white text-decoration-underline">umangkr871170@gmail.com</a></span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-telephone-fill text-warning fs-5"></i>
                        <span><strong>Technical Helpdesk:</strong> <a href="tel:08709878369" class="text-white">08709878369</a> / <a href="tel:8709878369" class="text-white">8709878369</a></span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-credit-card-2-front-fill text-warning fs-5"></i>
                        <span><strong>Payment Issues Helpline:</strong> <a href="tel:8709878369" class="text-white">8709878369</a> (<a href="mailto:adarshraj112008@gmail.com" class="text-white">adarshraj112008@gmail.com</a>)</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-headset text-warning fs-5"></i>
                        <span><strong>24/7 Customer Care:</strong> <a href="tel:7667046114" class="text-white">7667046114</a></span>
                    </div>
                </div>
            </div>

        </div>

        <!-- Footer Bottom Bar -->
        <div class="footer-bottom d-flex flex-column flex-md-row align-items-center justify-content-between gap-2">
            <div>
                © <%= java.time.Year.now().getValue() %> <strong>Apex University of Technology – Examination &amp; Academic Management System.</strong> All Rights Reserved.
            </div>
            <div>
                Built with Java MVC, Servlets, JDBC, JSP &amp; MySQL.
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom App JS -->
<script src="<%= cpFooter %>/static/js/main.js"></script>

</body>
</html>
