<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Apex University of Technology • Examination System'}</title>
    
    <!-- Favicon -->
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/static/images/favicon.svg">
    
    <!-- Bootstrap 5 CSS & Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <!-- Google Fonts: Inter & Montserrat -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Montserrat:wght@600;700;800;900&display=swap" rel="stylesheet">
    
    <!-- Custom Stylesheet -->
    <link rel="stylesheet" href="<%= cp %>/static/css/style.css">
</head>
<body>

<!-- University Navigation Header -->
<header class="apex-header">
    <div class="container-fluid px-lg-5">
        <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
            
            <!-- University Emblem Brand -->
            <a href="<%= cp %>/" class="apex-brand">
                <img src="<%= cp %>/static/images/logo.svg" alt="APEX University Logo" class="apex-brand-logo">
                <div>
                    <h1 class="apex-brand-title">Apex University of Technology</h1>
                    <p class="apex-brand-subtitle">Examination &amp; Academic Management System</p>
                </div>
            </a>

            <!-- Right Controls: Navigation, Theme Toggle, Auth -->
            <div class="d-flex align-items-center gap-2 flex-wrap">
                <nav class="d-none d-md-flex align-items-center gap-1">
                    <a href="<%= cp %>/" class="apex-nav-link ${pageContext.request.servletPath == '/home' || pageContext.request.servletPath == '/' ? 'active' : ''}">
                        <i class="bi bi-house-door me-1"></i> Home
                    </a>
                    <a href="<%= cp %>/result-search" class="apex-nav-link ${pageContext.request.servletPath == '/result-search' ? 'active' : ''}">
                        <i class="bi bi-award me-1"></i> Result Details
                    </a>
                    <a href="<%= cp %>/instructions" class="apex-nav-link ${pageContext.request.servletPath == '/instructions' ? 'active' : ''}">
                        <i class="bi bi-journal-text me-1"></i> Instructions
                    </a>
                    <a href="<%= cp %>/notices" class="apex-nav-link ${pageContext.request.servletPath == '/notices' ? 'active' : ''}">
                        <i class="bi bi-megaphone me-1"></i> Notices
                    </a>
                    <a href="<%= cp %>/helpdesk" class="apex-nav-link ${pageContext.request.servletPath == '/helpdesk' ? 'active' : ''}">
                        <i class="bi bi-headset me-1"></i> HelpDesk
                    </a>
                </nav>

                <!-- Theme Toggle Button -->
                <button type="button" class="btn-theme-toggle" id="themeToggleBtn" title="Toggle Light/Dark Mode">
                    <i class="bi bi-moon-stars-fill" id="themeToggleIcon"></i>
                    <span id="themeToggleLabel" class="d-none d-sm-inline">Dark Mode</span>
                </button>

                <!-- Portal Auth Buttons -->
                <c:choose>
                    <c:when test="${not empty sessionScope.CURRENT_USER}">
                        <div class="dropdown">
                            <button class="btn btn-apex-gold dropdown-toggle d-flex align-items-center gap-2 py-1 px-3" type="button" data-bs-toggle="dropdown">
                                <i class="bi bi-person-circle"></i>
                                <span>${sessionScope.CURRENT_USER.fullName}</span>
                                <span class="badge bg-dark">${sessionScope.CURRENT_USER.role}</span>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <c:if test="${sessionScope.CURRENT_USER.role == 'ADMIN'}">
                                    <li><a class="dropdown-item" href="<%= cp %>/admin/dashboard"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>
                                </c:if>
                                <c:if test="${sessionScope.CURRENT_USER.role == 'TEACHER'}">
                                    <li><a class="dropdown-item" href="<%= cp %>/teacher/dashboard"><i class="bi bi-speedometer2 me-2"></i>Teacher Dashboard</a></li>
                                </c:if>
                                <c:if test="${sessionScope.CURRENT_USER.role == 'STUDENT'}">
                                    <li><a class="dropdown-item" href="<%= cp %>/student/dashboard"><i class="bi bi-speedometer2 me-2"></i>Student Dashboard</a></li>
                                </c:if>
                                <c:if test="${sessionScope.CURRENT_USER.role == 'COLLEGE'}">
                                    <li><a class="dropdown-item" href="<%= cp %>/college/dashboard"><i class="bi bi-speedometer2 me-2"></i>College Dashboard</a></li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="<%= cp %>/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="<%= cp %>/login" class="btn btn-apex-gold d-flex align-items-center gap-1 py-1 px-3">
                            <i class="bi bi-box-arrow-in-right"></i> Portal Login
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</header>
