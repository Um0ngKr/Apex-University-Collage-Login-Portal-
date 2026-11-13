<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String cpSb = request.getContextPath();
    String currentPath = request.getRequestURI();
%>

<div class="dashboard-sidebar">
    <!-- User summary card -->
    <div class="sidebar-user">
        <div class="sidebar-user-name text-truncate">
            <i class="bi bi-person-badge-fill me-1 text-warning"></i> ${sessionScope.CURRENT_USER.fullName}
        </div>
        <div class="sidebar-user-role">
            ${sessionScope.CURRENT_USER.role} PORTAL
        </div>
        <small class="text-secondary text-truncate d-block mt-1">ID: ${sessionScope.CURRENT_USER.username}</small>
    </div>

    <!-- Navigation links according to user role -->
    <ul class="sidebar-nav">
        
        <!-- ================= ADMIN NAV ================= -->
        <c:if test="${sessionScope.CURRENT_USER.role == 'ADMIN'}">
            <li>
                <a href="<%= cpSb %>/admin/dashboard" class="sidebar-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-speedometer2"></i> Dashboard Overview
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/students" class="sidebar-link ${param.activeNav == 'students' ? 'active' : ''}">
                    <i class="bi bi-people-fill"></i> Student Management
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/teachers" class="sidebar-link ${param.activeNav == 'teachers' ? 'active' : ''}">
                    <i class="bi bi-person-video3"></i> Teacher Management
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/colleges" class="sidebar-link ${param.activeNav == 'colleges' ? 'active' : ''}">
                    <i class="bi bi-buildings-fill"></i> College Management
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/academics" class="sidebar-link ${param.activeNav == 'academics' ? 'active' : ''}">
                    <i class="bi bi-journal-bookmark-fill"></i> Depts &amp; Courses
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/examinations" class="sidebar-link ${param.activeNav == 'examinations' ? 'active' : ''}">
                    <i class="bi bi-calendar2-check-fill"></i> Exam Management
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/marks" class="sidebar-link ${param.activeNav == 'marks' ? 'active' : ''}">
                    <i class="bi bi-pencil-square"></i> Marks &amp; Results
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/attendance" class="sidebar-link ${param.activeNav == 'attendance' ? 'active' : ''}">
                    <i class="bi bi-check2-all"></i> Attendance Records
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/payments" class="sidebar-link ${param.activeNav == 'payments' ? 'active' : ''}">
                    <i class="bi bi-credit-card-2-front-fill"></i> Fee Payments
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/content" class="sidebar-link ${param.activeNav == 'content' ? 'active' : ''}">
                    <i class="bi bi-broadcast"></i> Content &amp; Notices
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/security" class="sidebar-link ${param.activeNav == 'security' ? 'active' : ''}">
                    <i class="bi bi-shield-lock-fill"></i> Security &amp; Audit
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/admin/profile" class="sidebar-link ${param.activeNav == 'profile' ? 'active' : ''}">
                    <i class="bi bi-person-gear"></i> Admin Profile
                </a>
            </li>
        </c:if>

        <!-- ================= TEACHER NAV ================= -->
        <c:if test="${sessionScope.CURRENT_USER.role == 'TEACHER'}">
            <li>
                <a href="<%= cpSb %>/teacher/dashboard" class="sidebar-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-speedometer2"></i> Dashboard Overview
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/teacher/attendance" class="sidebar-link ${param.activeNav == 'attendance' ? 'active' : ''}">
                    <i class="bi bi-check2-circle"></i> Mark Attendance
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/teacher/marks" class="sidebar-link ${param.activeNav == 'marks' ? 'active' : ''}">
                    <i class="bi bi-pencil-fill"></i> Upload Internal Marks
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/teacher/profile" class="sidebar-link ${param.activeNav == 'profile' ? 'active' : ''}">
                    <i class="bi bi-person-gear"></i> Teacher Profile
                </a>
            </li>
        </c:if>

        <!-- ================= STUDENT NAV ================= -->
        <c:if test="${sessionScope.CURRENT_USER.role == 'STUDENT'}">
            <li>
                <a href="<%= cpSb %>/student/dashboard" class="sidebar-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-speedometer2"></i> Student Overview
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/student/exam" class="sidebar-link ${param.activeNav == 'exam' ? 'active' : ''}">
                    <i class="bi bi-file-earmark-text-fill"></i> Examination Form
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/student/results" class="sidebar-link ${param.activeNav == 'results' ? 'active' : ''}">
                    <i class="bi bi-award-fill"></i> Academic Results
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/student/payments" class="sidebar-link ${param.activeNav == 'payments' ? 'active' : ''}">
                    <i class="bi bi-credit-card-fill"></i> Fee Payments
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/student/attendance" class="sidebar-link ${param.activeNav == 'attendance' ? 'active' : ''}">
                    <i class="bi bi-calendar-check-fill"></i> Attendance Details
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/student/profile" class="sidebar-link ${param.activeNav == 'profile' ? 'active' : ''}">
                    <i class="bi bi-person-bounding-box"></i> Student Profile
                </a>
            </li>
        </c:if>

        <!-- ================= COLLEGE NAV ================= -->
        <c:if test="${sessionScope.CURRENT_USER.role == 'COLLEGE'}">
            <li>
                <a href="<%= cpSb %>/college/dashboard" class="sidebar-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                    <i class="bi bi-speedometer2"></i> College Overview
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/college/students" class="sidebar-link ${param.activeNav == 'students' ? 'active' : ''}">
                    <i class="bi bi-person-plus-fill"></i> Student Registration
                </a>
            </li>
            <li>
                <a href="<%= cpSb %>/college/exam-forms" class="sidebar-link ${param.activeNav == 'exam-forms' ? 'active' : ''}">
                    <i class="bi bi-clipboard-check-fill"></i> Verify Exam Forms
                </a>
            </li>
        </c:if>

        <li class="mt-4"><hr class="border-secondary opacity-25"></li>
        <li>
            <a href="<%= cpSb %>/logout" class="sidebar-link text-danger">
                <i class="bi bi-box-arrow-right"></i> Sign Out
            </a>
        </li>
    </ul>
</div>
