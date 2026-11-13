<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Content & Circulars Management • Apex University" />
</jsp:include>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
        <jsp:param name="activeNav" value="content" />
    </jsp:include>

    <main class="dashboard-content">
        <div class="container-fluid">
            
            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                    <h2 class="h3 fw-bold mb-1">Content, Instructions &amp; Helpdesk Management</h2>
                    <p class="text-secondary small mb-0">Manage published student instructions, university circulars, and official helpline directory entries.</p>
                </div>
            </div>

            <!-- Tab Buttons -->
            <ul class="nav nav-pills mb-4 p-1 bg-light rounded-3">
                <li class="nav-item">
                    <a class="nav-link ${empty activeTab || activeTab == 'instructions' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/content?tab=instructions">
                        <i class="bi bi-journal-text me-1"></i> Instructions (${instructions.size()})
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'notices' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/content?tab=notices">
                        <i class="bi bi-megaphone me-1"></i> News &amp; Circulars (${notices.size()})
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab == 'helpdesk' ? 'active bg-primary' : 'text-dark'}" href="${pageContext.request.contextPath}/admin/content?tab=helpdesk">
                        <i class="bi bi-headset me-1"></i> HelpDesk Directory (${helpdeskList.size()})
                    </a>
                </li>
            </ul>

            <!-- ================= TAB: INSTRUCTIONS ================= -->
            <c:if test="${empty activeTab || activeTab == 'instructions'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Examination Instructions</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addInstructionModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Instruction
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>Title</th>
                                    <th>Content Preview</th>
                                    <th>Order</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="inst" items="${instructions}">
                                    <tr>
                                        <td><span class="badge bg-primary">${inst.category}</span></td>
                                        <td class="fw-bold">${inst.title}</td>
                                        <td><small class="text-muted">${inst.content.length() > 90 ? inst.content.substring(0, 90) : inst.content}...</small></td>
                                        <td>${inst.sortOrder}</td>
                                        <td>
                                            <span class="badge ${inst.published ? 'bg-success' : 'bg-secondary'}">
                                                ${inst.published ? 'Published' : 'Draft'}
                                            </span>
                                        </td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${inst.id}"
                                                        data-category="<c:out value='${inst.category}' />"
                                                        data-title="<c:out value='${inst.title}' />"
                                                        data-content="<c:out value='${inst.content}' />"
                                                        data-order="${inst.sortOrder}"
                                                        data-published="${inst.published}"
                                                        onclick="populateEditInstruction(this)"
                                                        title="Edit Instruction">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/content-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="type" value="instruction">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${inst.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete instruction?" title="Delete Instruction">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ================= TAB: NOTICES ================= -->
            <c:if test="${activeTab == 'notices'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Official University Bulletins &amp; Notices</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addNoticeModal">
                            <i class="bi bi-plus-lg me-1"></i> Publish Notice
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>Notice Title</th>
                                    <th>Audience</th>
                                    <th>Publication Date</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="n" items="${notices}">
                                    <tr>
                                        <td><span class="badge bg-danger">${n.category}</span></td>
                                        <td class="fw-bold">${n.title}</td>
                                        <td><span class="badge bg-light text-dark border">${n.targetAudience}</span></td>
                                        <td><small>${n.publishedAt}</small></td>
                                        <td><span class="badge ${n.published ? 'bg-success' : 'bg-secondary'}">${n.published ? 'Live' : 'Hidden'}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${n.id}"
                                                        data-title="<c:out value='${n.title}' />"
                                                        data-category="<c:out value='${n.category}' />"
                                                        data-audience="<c:out value='${n.targetAudience}' />"
                                                        data-description="<c:out value='${n.description}' />"
                                                        data-attachment="<c:out value='${n.attachmentUrl}' />"
                                                        data-published="${n.published}"
                                                        onclick="populateEditNotice(this)"
                                                        title="Edit Notice">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/content-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="type" value="notice">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${n.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete notice ${n.title}?" title="Delete Notice">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

            <!-- ================= TAB: HELPDESK ================= -->
            <c:if test="${activeTab == 'helpdesk'}">
                <div class="card shadow-sm border rounded-4 p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h5 fw-bold mb-0">Official University Helplines &amp; Support Contacts</h4>
                        <button type="button" class="btn btn-apex-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addHelpdeskModal">
                            <i class="bi bi-plus-lg me-1"></i> Add Contact Desk
                        </button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0 apex-table apex-table-compact">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>HelpDesk Title</th>
                                    <th>Officer / Desk</th>
                                    <th>Phone (Clickable)</th>
                                    <th>Email (Clickable)</th>
                                    <th>Office Address</th>
                                    <th>Status</th>
                                    <th class="text-center text-nowrap" style="width: 110px; min-width: 110px;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="h" items="${helpdeskList}">
                                    <tr>
                                        <td><span class="badge bg-warning text-dark">${h.category}</span></td>
                                        <td class="fw-bold">${h.title}</td>
                                        <td>${h.contactPerson}</td>
                                        <td>
                                            <a href="tel:${h.phoneNumber}" class="fw-bold text-decoration-none">
                                                <i class="bi bi-telephone-fill me-1"></i> ${h.phoneNumber}
                                            </a>
                                        </td>
                                        <td>
                                            <a href="mailto:${h.email}" class="text-decoration-underline text-break">
                                                <i class="bi bi-envelope-fill me-1"></i> ${h.email}
                                            </a>
                                        </td>
                                        <td><small class="text-muted">${h.address}</small></td>
                                        <td><span class="badge ${h.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${h.status}</span></td>
                                        <td class="text-center text-nowrap">
                                            <div class="table-actions">
                                                <button type="button" class="btn btn-sm btn-outline-primary btn-icon-only"
                                                        data-id="${h.id}"
                                                        data-category="<c:out value='${h.category}' />"
                                                        data-title="<c:out value='${h.title}' />"
                                                        data-person="<c:out value='${h.contactPerson}' />"
                                                        data-phone="<c:out value='${h.phoneNumber}' />"
                                                        data-email="<c:out value='${h.email}' />"
                                                        data-timing="<c:out value='${h.timing}' />"
                                                        data-address="<c:out value='${h.address}' />"
                                                        data-order="${h.sortOrder}"
                                                        data-status="${h.status}"
                                                        onclick="populateEditHelpdesk(this)"
                                                        title="Edit Support Contact">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/content-action" method="POST" class="d-inline">
                                                    <input type="hidden" name="type" value="helpdesk">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${h.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-icon-only btn-confirm-delete" data-confirm="Delete contact ${h.title}?" title="Delete Contact">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>

        </div>
    </main>
</div>

<!-- Modal: Add Instruction -->
<div class="modal fade" id="addInstructionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Add Instruction Guideline</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="instruction">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Category</label>
                        <select name="category" class="form-select">
                            <option value="Exam Form Filling Instructions">Exam Form Filling Instructions</option>
                            <option value="Online Fee Payment Instructions">Online Fee Payment Instructions</option>
                            <option value="Admit Card Download Instructions">Admit Card Download Instructions</option>
                            <option value="Result Checking Instructions">Result Checking Instructions</option>
                            <option value="Registration Instructions">Registration Instructions</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Title</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Content</label>
                        <textarea name="content" class="form-control" rows="4" required></textarea>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Sort Order</label>
                            <input type="number" name="sort_order" class="form-control" value="1" required>
                        </div>
                        <div class="col-6 d-flex align-items-center pt-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="is_published" value="1" checked id="instPublishCheck">
                                <label class="form-check-label" for="instPublishCheck">Publish Live</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Instruction</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Notice -->
<div class="modal fade" id="addNoticeModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold">Publish University Notice</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="notice">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notice Title</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Category</label>
                            <select name="category" class="form-select">
                                <option value="EXAMINATION">EXAMINATION</option>
                                <option value="RESULT">RESULT</option>
                                <option value="ADMISSION">ADMISSION</option>
                                <option value="GENERAL">GENERAL</option>
                                <option value="URGENT">URGENT</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Target Audience</label>
                            <select name="target_audience" class="form-select">
                                <option value="ALL">ALL</option>
                                <option value="STUDENTS">STUDENTS ONLY</option>
                                <option value="TEACHERS">TEACHERS ONLY</option>
                                <option value="COLLEGES">COLLEGES ONLY</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notice Description</label>
                        <textarea name="description" class="form-control" rows="3" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Attachment URL</label>
                        <input type="text" name="attachment_url" class="form-control" placeholder="https://... or notice.pdf">
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" name="is_published" value="1" checked id="noticePublishCheck">
                        <label class="form-check-label" for="noticePublishCheck">Publish Live on Portals</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Publish Notice</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Instruction -->
<div class="modal fade" id="editInstructionModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">Edit Instruction Guideline</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="instruction">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editInstructionId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Category</label>
                        <select name="category" id="editInstructionCategory" class="form-select">
                            <option value="Exam Form Filling Instructions">Exam Form Filling Instructions</option>
                            <option value="Online Fee Payment Instructions">Online Fee Payment Instructions</option>
                            <option value="Admit Card Download Instructions">Admit Card Download Instructions</option>
                            <option value="Result Checking Instructions">Result Checking Instructions</option>
                            <option value="Registration Instructions">Registration Instructions</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Title</label>
                        <input type="text" name="title" id="editInstructionTitle" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Content</label>
                        <textarea name="content" id="editInstructionContent" class="form-control" rows="4" required></textarea>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Sort Order</label>
                            <input type="number" name="sort_order" id="editInstructionOrder" class="form-control" value="1" required>
                        </div>
                        <div class="col-6 d-flex align-items-center pt-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="is_published" value="1" id="editInstructionPublished">
                                <label class="form-check-label" for="editInstructionPublished">Publish Live</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Instruction</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Notice -->
<div class="modal fade" id="editNoticeModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold">Edit University Notice</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="notice">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editNoticeId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notice Title</label>
                        <input type="text" name="title" id="editNoticeTitle" class="form-control" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Category</label>
                            <select name="category" id="editNoticeCategory" class="form-select">
                                <option value="EXAMINATION">EXAMINATION</option>
                                <option value="RESULT">RESULT</option>
                                <option value="ADMISSION">ADMISSION</option>
                                <option value="GENERAL">GENERAL</option>
                                <option value="URGENT">URGENT</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Target Audience</label>
                            <select name="target_audience" id="editNoticeAudience" class="form-select">
                                <option value="ALL">ALL</option>
                                <option value="STUDENTS">STUDENTS ONLY</option>
                                <option value="TEACHERS">TEACHERS ONLY</option>
                                <option value="COLLEGES">COLLEGES ONLY</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Notice Description</label>
                        <textarea name="description" id="editNoticeDescription" class="form-control" rows="3" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Attachment URL</label>
                        <input type="text" name="attachment_url" id="editNoticeAttachment" class="form-control" placeholder="https://... or notice.pdf">
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" name="is_published" value="1" id="editNoticePublished">
                        <label class="form-check-label" for="editNoticePublished">Publish Live on Portals</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">Update Notice</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Add Helpdesk Contact -->
<div class="modal fade" id="addHelpdeskModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title fw-bold">Add HelpDesk Contact Record</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="helpdesk">
                <input type="hidden" name="action" value="add">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Category</label>
                        <select name="category" class="form-select">
                            <option value="PAYMENT">PAYMENT</option>
                            <option value="TECHNICAL">TECHNICAL</option>
                            <option value="QUERIES">QUERIES</option>
                            <option value="GENERAL">GENERAL</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Title</label>
                        <input type="text" name="title" class="form-control" placeholder="e.g. Online Payment Issues" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Contact Person / Officer</label>
                        <input type="text" name="contact_person" class="form-control" placeholder="e.g. Accounts Officer">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Phone Number</label>
                            <input type="text" name="phone_number" class="form-control" placeholder="e.g. 8709878369">
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" name="email" class="form-control" placeholder="support@apex.ac.in">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Timing</label>
                        <input type="text" name="timing" class="form-control" value="10:00 AM - 05:00 PM">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Address / Campus Room</label>
                        <input type="text" name="address" class="form-control" value="Kaikhali Main Rd">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Sort Order</label>
                        <input type="number" name="sort_order" class="form-control" value="1">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-warning">Save Contact</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: Edit Helpdesk Contact -->
<div class="modal fade" id="editHelpdeskModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title fw-bold">Edit HelpDesk Contact Record</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/content-action" method="POST">
                <input type="hidden" name="type" value="helpdesk">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editHelpdeskId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Category</label>
                        <select name="category" id="editHelpdeskCategory" class="form-select">
                            <option value="PAYMENT">PAYMENT</option>
                            <option value="TECHNICAL">TECHNICAL</option>
                            <option value="QUERIES">QUERIES</option>
                            <option value="GENERAL">GENERAL</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Title</label>
                        <input type="text" name="title" id="editHelpdeskTitle" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Contact Person / Officer</label>
                        <input type="text" name="contact_person" id="editHelpdeskPerson" class="form-control">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Phone Number</label>
                            <input type="text" name="phone_number" id="editHelpdeskPhone" class="form-control">
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" name="email" id="editHelpdeskEmail" class="form-control">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Timing</label>
                        <input type="text" name="timing" id="editHelpdeskTiming" class="form-control">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Address / Campus Room</label>
                        <input type="text" name="address" id="editHelpdeskAddress" class="form-control">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold">Sort Order</label>
                            <input type="number" name="sort_order" id="editHelpdeskOrder" class="form-control" value="1">
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold">Status</label>
                            <select name="status" id="editHelpdeskStatus" class="form-select">
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-warning">Update Contact</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function populateEditInstruction(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editInstructionId').value = d.id || '';
    document.getElementById('editInstructionCategory').value = d.category || '';
    document.getElementById('editInstructionTitle').value = d.title || '';
    document.getElementById('editInstructionContent').value = d.content || '';
    document.getElementById('editInstructionOrder').value = d.order || '1';
    const pubCheck = document.getElementById('editInstructionPublished');
    if (pubCheck) {
        pubCheck.checked = (d.published === 'true' || d.published === '1');
    }
    new bootstrap.Modal(document.getElementById('editInstructionModal')).show();
}

function populateEditNotice(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editNoticeId').value = d.id || '';
    document.getElementById('editNoticeTitle').value = d.title || '';
    document.getElementById('editNoticeCategory').value = d.category || 'EXAMINATION';
    document.getElementById('editNoticeAudience').value = d.audience || 'ALL';
    document.getElementById('editNoticeDescription').value = d.description || '';
    document.getElementById('editNoticeAttachment').value = d.attachment || '';
    const pubCheck = document.getElementById('editNoticePublished');
    if (pubCheck) {
        pubCheck.checked = (d.published === 'true' || d.published === '1');
    }
    new bootstrap.Modal(document.getElementById('editNoticeModal')).show();
}

function populateEditHelpdesk(elem) {
    if (!elem) return;
    const d = elem.dataset;
    document.getElementById('editHelpdeskId').value = d.id || '';
    document.getElementById('editHelpdeskCategory').value = d.category || 'GENERAL';
    document.getElementById('editHelpdeskTitle').value = d.title || '';
    document.getElementById('editHelpdeskPerson').value = d.person || '';
    document.getElementById('editHelpdeskPhone').value = d.phone || '';
    document.getElementById('editHelpdeskEmail').value = d.email || '';
    document.getElementById('editHelpdeskTiming').value = d.timing || '';
    document.getElementById('editHelpdeskAddress').value = d.address || '';
    document.getElementById('editHelpdeskOrder').value = d.order || '1';
    document.getElementById('editHelpdeskStatus').value = d.status || 'ACTIVE';
    new bootstrap.Modal(document.getElementById('editHelpdeskModal')).show();
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
