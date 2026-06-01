package com.enterprise.meeting.presentation.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

// ===== Data models =====

data class Employee(
    val id: Long,
    val name: String,
    val role: String,
    val phone: String,
    val title: String,
    val isActive: Boolean,
    val password: String = "",
)

data class Group(
    val id: Long,
    val name: String,
    val leaderName: String,
    val employees: List<Employee>,
    val isActive: Boolean,
)

data class Department(
    val id: Long,
    val name: String,
    val leaderName: String,
    val groups: List<Group>,
    val isActive: Boolean,
)

// ===== Mock data =====

private val mockEmployees = listOf(
    Employee(101, "张三", "工程师", "138 0010 0001", "高级工程师", true),
    Employee(102, "李四", "工程师", "138 0010 0002", "中级工程师", true),
    Employee(103, "王五", "工程师", "138 0010 0003", "初级工程师", true),
    Employee(104, "钱六", "工程师", "138 0010 0004", "高级工程师", true),
    Employee(105, "周七", "工程师", "138 0010 0005", "中级工程师", true),
    Employee(201, "赵设计", "执行人员", "139 1234 0021", "高级设计师", true),
    Employee(202, "钱设计", "执行人员", "139 1234 0022", "中级设计师", true),
    Employee(203, "孙策划", "执行人员", "139 1234 0023", "高级策划师", true),
    Employee(204, "李策划", "执行人员", "139 1234 0024", "中级策划师", true),
    Employee(301, "周运营", "执行人员", "186 7788 0031", "运营主管", true),
    Employee(302, "吴运营", "执行人员", "186 7788 0032", "运营专员", true),
    Employee(303, "郑运营", "执行人员", "186 7788 0033", "运营专员", true),
    Employee(401, "阿布都热合曼", "执行人员", "159 9920 0041", "市场专员", true),
    Employee(402, "陈市场", "执行人员", "159 9920 0042", "市场主管", true),
    Employee(403, "林品牌", "执行人员", "159 9920 0043", "品牌专员", false),
    Employee(501, "陈晓东", "执行人员", "133 4400 0051", "行政专员", false),
)

private val mockDepartments = listOf(
    Department(1, "信息技术部", "张伟", listOf(
        Group(11, "研发一组", "赵刚", mockEmployees.filter { it.id in 101..103 }, true),
        Group(12, "研发二组", "孙强", mockEmployees.filter { it.id in 104..105 }, true),
    ), true),
    Department(2, "产品部", "李明", listOf(
        Group(21, "产品设计组", "李明", mockEmployees.filter { it.id in 201..202 }, true),
        Group(22, "产品策划组", "王思雨", mockEmployees.filter { it.id in 203..204 }, true),
    ), true),
    Department(3, "运营部", "王思雨", listOf(
        Group(31, "运营一组", "周运营", mockEmployees.filter { it.id in 301..302 }, true),
        Group(32, "运营二组", "吴运营", mockEmployees.filter { it.id in 303..303 }, true),
    ), true),
    Department(4, "市场部", "阿布都热合曼", listOf(
        Group(41, "市场推广组", "阿布都热合曼", mockEmployees.filter { it.id in 401..402 }, true),
        Group(42, "品牌策划组", "陈市场", mockEmployees.filter { it.id in 403..403 }, false),
    ), true),
    Department(5, "行政部", "陈晓东", listOf(
        Group(51, "综合行政组", "陈晓东", mockEmployees.filter { it.id in 501..501 }, false),
    ), false),
)

// ===== Main screen =====

private fun allEmployeesList(departments: List<Department>): List<Employee> =
    departments.flatMap { dept -> dept.groups.flatMap { group -> group.employees } }

private fun employeeLocationLabel(departments: List<Department>, emp: Employee): String {
    for (dept in departments) {
        for (group in dept.groups) {
            if (group.employees.any { it.id == emp.id }) {
                return "${emp.name} · ${dept.name} · ${group.name} · ${emp.role}"
            }
        }
    }
    return "${emp.name} · ${emp.role}"
}

private fun findEmployeeDeptGroup(departments: List<Department>, empId: Long): Pair<String, String>? {
    for (dept in departments) {
        for (group in dept.groups) {
            if (group.employees.any { it.id == empId }) {
                return dept.name to group.name
            }
        }
    }
    return null
}

private data class EmpGroupInput(val name: String, val leaderName: String = "")
private data class DeptCreateInfo(
    val name: String,
    val leaderName: String,
    val groups: List<EmpGroupInput>,
)

private fun removeEmployeeFromDept(departments: MutableList<Department>, empId: Long) {
    val deptIdx = departments.indexOfFirst { dept ->
        dept.groups.any { group -> group.employees.any { it.id == empId } }
    }
    if (deptIdx >= 0) {
        val dept = departments[deptIdx]
        val updatedGroups = dept.groups.map { group ->
            if (group.employees.any { it.id == empId }) {
                group.copy(employees = group.employees.filter { it.id != empId })
            } else group
        }
        departments[deptIdx] = dept.copy(groups = updatedGroups)
    }
}

private var nextDeptId = 100L
private var nextEmpId = 1000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrgManagementScreen(
    onBack: () -> Unit = {},
    onNewDepartment: () -> Unit = {},
    onNewEmployee: () -> Unit = {},
) {
    val role = LocalRoleColors.current
    var searchQuery by remember { mutableStateOf("") }
    val expandedDepts = remember { mutableStateMapOf<Long, Boolean>() }
    val expandedGroups = remember { mutableStateMapOf<Long, Boolean>() }
    var showNewDeptDialog by remember { mutableStateOf(false) }
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showNewEmpDialog by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }
    var pendingDeptConfirm by remember { mutableStateOf<DeptCreateInfo?>(null) }
    var editingDept by remember { mutableStateOf<Department?>(null) }
    var editingGroupInfo by remember { mutableStateOf<Pair<Long, Group>?>(null) }
    var editingEmpInfo by remember { mutableStateOf<Triple<Long, Long, Employee>?>(null) }

    // Use mutable state so newly created items persist
    val departments = remember { mutableStateListOf<Department>() }
    LaunchedEffect(Unit) { if (departments.isEmpty()) departments.addAll(mockDepartments) }

    val allEmployees = remember { derivedStateOf { allEmployeesList(departments) } }
    val isSearching = searchQuery.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "管理后台",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = role.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = role.primary,
                )
            )
        },
        containerColor = pageBg,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Search bar
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        role = role,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Section header
                item {
                    val total = allEmployees.value.size
                    Text(
                        "远航科技集团 · ${departments.size} 个部门 · $total 名员工",
                        fontSize = 12.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                    )
                }

                // Department cards
                val displayDepts = departments.filter { dept ->
                    if (!isSearching) true
                    else dept.name.contains(searchQuery) ||
                            dept.groups.any { group ->
                                group.name.contains(searchQuery) ||
                                        group.employees.any { it.name.contains(searchQuery) }
                            }
                }

                items(displayDepts, key = { it.id }) { dept ->
                    DepartmentCard(
                        department = dept,
                        isExpanded = expandedDepts[dept.id] ?: isSearching,
                        expandedGroups = expandedGroups,
                        searchQuery = searchQuery,
                        isSearching = isSearching,
                        onToggleDept = {
                            expandedDepts[dept.id] = !(expandedDepts[dept.id] ?: false)
                        },
                        onToggleGroup = { groupId ->
                            expandedGroups[groupId] = !(expandedGroups[groupId] ?: false)
                        },
                        onEditDept = { editingDept = departments.find { it.id == dept.id } },
                        onEditGroup = { deptId, group -> editingGroupInfo = deptId to group },
                        onEditEmployee = { deptId, groupId, emp -> editingEmpInfo = Triple(deptId, groupId, emp) },
                        role = role,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Bottom spacer for FABs
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // FAB speed dial
            Column(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Menu items (visible when expanded)
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SpeedDialItem("新增人员", Icons.Filled.PersonAdd) {
                            fabExpanded = false; showNewEmpDialog = true
                        }
                        SpeedDialItem("新建分组", Icons.Filled.CreateNewFolder) {
                            fabExpanded = false; showNewGroupDialog = true
                        }
                        SpeedDialItem("新建部门", Icons.Filled.AccountBalance) {
                            fabExpanded = false; showNewDeptDialog = true
                        }
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    shape = CircleShape,
                    containerColor = role.primary,
                    contentColor = role.onPrimary,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        if (fabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "新增",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }

    // ===== New Department Dialog =====
    if (showNewDeptDialog) {
        val existingNames = remember { departments.map { it.name }.toSet() }
        NewDepartmentDialog(
            departments = departments.toList(),
            existingNames = existingNames,
            allEmployees = allEmployees.value,
            role = role,
            onDismiss = { showNewDeptDialog = false },
            onConfirm = { name, leaderName, groups ->
                showNewDeptDialog = false
                pendingDeptConfirm = DeptCreateInfo(name, leaderName, groups)
            }
        )
    }

    // ===== Confirm Department Creation (final confirmation) =====
    pendingDeptConfirm?.let { info ->
        val leaderEmployee = allEmployees.value.find { it.name == info.leaderName }
        AlertDialog(
            onDismissRequest = { pendingDeptConfirm = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = surfaceWhite,
            title = {
                Text("确认创建部门", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("部门名称：${info.name}", fontSize = 14.sp, color = textPrimary)
                    if (info.leaderName.isNotBlank()) {
                        Text("负责人：${info.leaderName}", fontSize = 14.sp, color = textPrimary)
                    }
                    Text("分组（${info.groups.size}个）：${info.groups.joinToString("、") { it.name }}", fontSize = 13.sp, color = textSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingDeptConfirm = null
                        val newId = nextDeptId++

                        // Move leader employee from original group
                        if (leaderEmployee != null && info.leaderName.isNotBlank()) {
                            removeEmployeeFromDept(departments, leaderEmployee.id)
                        }

                        // Create groups
                        val groups = info.groups.mapIndexed { idx, g ->
                            val gId = newId * 10 + (idx + 1).toLong()
                            val leaderEmp = if (g.leaderName.isNotBlank()) {
                                allEmployees.value.find { it.name == g.leaderName }
                            } else null
                            if (leaderEmp != null) {
                                removeEmployeeFromDept(departments, leaderEmp.id)
                            }
                            val empList = if (leaderEmp != null) listOf(leaderEmp) else emptyList()
                            Group(gId, g.name, g.leaderName, empList, true)
                        }

                        val leaderEmpList = if (leaderEmployee != null) listOf(leaderEmployee) else emptyList()
                        // If no groups specified, create a default one
                        val finalGroups = if (groups.isEmpty()) {
                            listOf(Group(newId * 10 + 1, "默认分组", info.leaderName, leaderEmpList, true))
                        } else {
                            // Add leader to first group if not already assigned to any group
                            if (leaderEmployee != null && groups.none { g -> g.leaderName == info.leaderName } && groups.isNotEmpty()) {
                                val firstGroup = groups[0]
                                groups.toMutableList().apply {
                                    set(0, firstGroup.copy(employees = firstGroup.employees + leaderEmployee))
                                }
                            } else groups
                        }

                        val dept = Department(
                            id = newId,
                            name = info.name,
                            leaderName = info.leaderName,
                            groups = finalGroups,
                            isActive = true,
                        )
                        departments.add(dept)
                        expandedDepts[newId] = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                ) { Text("确认创建") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeptConfirm = null }) {
                    Text("取消", color = textSecondary)
                }
            },
        )
    }

    // ===== New Employee Dialog =====
    if (showNewEmpDialog) {
        val existingNames = remember { allEmployees.value.map { it.name }.toSet() }
        val existingPhones = remember { allEmployees.value.map { it.phone }.toSet() }
        NewEmployeeDialog(
            departments = departments.toList(),
            allEmployees = allEmployees.value,
            existingNames = existingNames,
            existingPhones = existingPhones,
            role = role,
            onDismiss = { showNewEmpDialog = false },
            onConfirm = { name, roleName, phone, title, password, deptId, groupId ->
                val empId = nextEmpId++
                val emp = Employee(empId, name, roleName, phone, title, true, password)
                val deptIdx = departments.indexOfFirst { it.id == deptId }
                if (deptIdx >= 0) {
                    val dept = departments[deptIdx]
                    val groupIdx = dept.groups.indexOfFirst { it.id == groupId }
                    if (groupIdx >= 0) {
                        val group = dept.groups[groupIdx]
                        val updatedGroup = group.copy(employees = group.employees + emp)
                        val updatedGroups = dept.groups.toMutableList().apply { set(groupIdx, updatedGroup) }
                        departments[deptIdx] = dept.copy(groups = updatedGroups)
                    }
                }
                expandedDepts[deptId] = true
                showNewEmpDialog = false
            }
        )
    }

    // ===== New Group Dialog =====
    if (showNewGroupDialog) {
        NewGroupDialog(
            departments = departments.toList(),
            role = role,
            onDismiss = { showNewGroupDialog = false },
            onConfirm = { groupName, leaderName, deptId ->
                val newGroupId = (deptId * 10 + 1) + (departments.find { it.id == deptId }?.groups?.size ?: 0)
                val group = Group(newGroupId, groupName, leaderName, emptyList(), true)
                val deptIdx = departments.indexOfFirst { it.id == deptId }
                if (deptIdx >= 0) {
                    val dept = departments[deptIdx]
                    departments[deptIdx] = dept.copy(groups = dept.groups + group)
                    expandedGroups[newGroupId] = true
                }
                showNewGroupDialog = false
            }
        )
    }

    // ===== Edit Department Dialog =====
    editingDept?.let { dept ->
        var editName by remember(dept.id) { mutableStateOf(dept.name) }
        var editLeaderName by remember(dept.id) { mutableStateOf(dept.leaderName) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showMoveConfirm by remember { mutableStateOf(false) }
        var nameError by remember { mutableStateOf<String?>(null) }
        var leaderExpanded by remember { mutableStateOf(false) }
        val existingNames = remember { departments.map { it.name }.filter { it != dept.name }.toSet() }
        val activeEmps = remember { allEmployees.value.filter { it.isActive } }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("删除部门", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = { Text("确定删除「${dept.name}」及其所有分组和员工？此操作不可撤销。", fontSize = 14.sp, color = textPrimary) },
                confirmButton = {
                    Button(onClick = { departments.removeAll { it.id == dept.id }; editingDept = null },
                        shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = colorDanger)
                    ) { Text("删除", color = Color.White) }
                },
                dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }

        if (showMoveConfirm) {
            val movedEmp = activeEmps.find { it.name == editLeaderName }
            AlertDialog(
                onDismissRequest = { showMoveConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("移出确认", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = {
                    Text(
                        "「${editLeaderName}」当前${movedEmp?.let { employeeLocationLabel(departments.toList(), it) }?.substringAfter("·") ?: ""}，确定将其移入「${dept.name}」？",
                        fontSize = 14.sp, color = textPrimary,
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val idx = departments.indexOfFirst { it.id == dept.id }
                        if (idx >= 0) {
                            val leaderEmp = allEmployees.value.find { it.name == editLeaderName && it.isActive }
                            if (leaderEmp != null) {
                                removeEmployeeFromDept(departments, leaderEmp.id)
                                val targetGroup = departments[idx].groups.firstOrNull()
                                if (targetGroup != null) {
                                    val gIdx = departments[idx].groups.indexOfFirst { it.id == targetGroup.id }
                                    if (gIdx >= 0) {
                                        val updatedEmps = departments[idx].groups[gIdx].employees + leaderEmp
                                        val updatedGroups = departments[idx].groups.toMutableList().apply {
                                            set(gIdx, departments[idx].groups[gIdx].copy(employees = updatedEmps))
                                        }
                                        departments[idx] = departments[idx].copy(name = editName, leaderName = editLeaderName, groups = updatedGroups)
                                    }
                                }
                            } else {
                                departments[idx] = departments[idx].copy(name = editName, leaderName = editLeaderName)
                            }
                        }
                        editingDept = null
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) { Text("确认移入") }
                },
                dismissButton = { TextButton(onClick = { showMoveConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }

        AlertDialog(
            onDismissRequest = { editingDept = null },
            shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
            title = { Text("编辑部门", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = editName, onValueChange = {
                        editName = it; nameError = when { it.isBlank() -> null; it in existingNames -> "同名部门已存在"; else -> null }
                    }, label = { Text("部门名称") }, isError = nameError != null,
                        supportingText = nameError?.let { { Text(it, color = colorDanger, fontSize = 11.sp) } },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary, errorBorderColor = colorDanger, errorLabelColor = colorDanger),
                    )
                    ExposedDropdownMenuBox(expanded = leaderExpanded, onExpandedChange = { leaderExpanded = it }) {
                        OutlinedTextField(
                            value = editLeaderName, onValueChange = {}, readOnly = true,
                            label = { Text("负责人（可选）") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        ExposedDropdownMenu(expanded = leaderExpanded, onDismissRequest = { leaderExpanded = false }) {
                            DropdownMenuItem(text = { Text("（不指定）", fontSize = 13.sp, color = textSecondary) }, onClick = { editLeaderName = ""; leaderExpanded = false })
                            activeEmps.forEach { emp ->
                                DropdownMenuItem(
                                    text = { Text(employeeLocationLabel(departments.toList(), emp), fontSize = 13.sp) },
                                    onClick = { editLeaderName = emp.name; leaderExpanded = false },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = { showDeleteConfirm = true }, colors = ButtonDefaults.textButtonColors(contentColor = colorDanger)) {
                        Icon(Icons.Filled.Delete, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("删除此部门", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editLeaderName.isNotBlank() && editLeaderName != dept.leaderName) {
                        showMoveConfirm = true
                    } else {
                        val idx = departments.indexOfFirst { it.id == dept.id }
                        if (idx >= 0) departments[idx] = departments[idx].copy(name = editName, leaderName = editLeaderName)
                        editingDept = null
                    }
                }, enabled = editName.isNotBlank(), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                ) { Text("保存") }
            },
            dismissButton = { TextButton(onClick = { editingDept = null }) { Text("取消", color = textSecondary) } },
        )
    }

    // ===== Edit Group Dialog =====
    editingGroupInfo?.let { (deptId, group) ->
        val dept = departments.find { it.id == deptId }
        var editGroupName by remember(group.id) { mutableStateOf(group.name) }
        var editGroupLeader by remember(group.id) { mutableStateOf(group.leaderName) }
        var leaderExpanded by remember { mutableStateOf(false) }
        var showMoveConfirm by remember { mutableStateOf(false) }
        val activeEmps = remember { allEmployees.value.filter { it.isActive } }

        AlertDialog(
            onDismissRequest = { editingGroupInfo = null },
            shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
            title = { Text("编辑分组", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (dept != null) Text("所属部门：${dept.name}", fontSize = 13.sp, color = textSecondary)
                    OutlinedTextField(value = editGroupName, onValueChange = { editGroupName = it },
                        label = { Text("分组名称") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenuBox(expanded = leaderExpanded, onExpandedChange = { leaderExpanded = it }) {
                        OutlinedTextField(
                            value = editGroupLeader, onValueChange = {}, readOnly = true,
                            label = { Text("组长（可选）") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        ExposedDropdownMenu(expanded = leaderExpanded, onDismissRequest = { leaderExpanded = false }) {
                            DropdownMenuItem(text = { Text("（不指定）", fontSize = 13.sp, color = textSecondary) }, onClick = { editGroupLeader = ""; leaderExpanded = false })
                            activeEmps.forEach { emp ->
                                DropdownMenuItem(
                                    text = { Text(employeeLocationLabel(departments.toList(), emp), fontSize = 13.sp) },
                                    onClick = { editGroupLeader = emp.name; leaderExpanded = false },
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editGroupLeader.isNotBlank() && editGroupLeader != group.leaderName) {
                        showMoveConfirm = true
                    } else {
                        val dIdx = departments.indexOfFirst { it.id == deptId }
                        if (dIdx >= 0) {
                            val gIdx = departments[dIdx].groups.indexOfFirst { it.id == group.id }
                            if (gIdx >= 0) {
                                val updatedGroups = departments[dIdx].groups.toMutableList()
                                updatedGroups[gIdx] = group.copy(name = editGroupName, leaderName = editGroupLeader)
                                departments[dIdx] = departments[dIdx].copy(groups = updatedGroups)
                            }
                        }
                        editingGroupInfo = null
                    }
                }, enabled = editGroupName.isNotBlank(), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                ) { Text("保存") }
            },
            dismissButton = { TextButton(onClick = { editingGroupInfo = null }) { Text("取消", color = textSecondary) } },
        )

        if (showMoveConfirm) {
            val movedEmp = activeEmps.find { it.name == editGroupLeader }
            AlertDialog(
                onDismissRequest = { showMoveConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("移出确认", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = {
                    Text(
                        "「${editGroupLeader}」当前${movedEmp?.let { employeeLocationLabel(departments.toList(), it) }?.substringAfter("·") ?: ""}，确定将其移入「${group.name}」？",
                        fontSize = 14.sp, color = textPrimary,
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val dIdx = departments.indexOfFirst { it.id == deptId }
                        if (dIdx >= 0) {
                            val leaderEmp = allEmployees.value.find { it.name == editGroupLeader && it.isActive }
                            if (leaderEmp != null) {
                                removeEmployeeFromDept(departments, leaderEmp.id)
                                val d2 = departments.indexOfFirst { it.id == deptId }
                                if (d2 >= 0) {
                                    val g2 = departments[d2].groups.indexOfFirst { it.id == group.id }
                                    if (g2 >= 0) {
                                        val updatedEmps = departments[d2].groups[g2].employees + leaderEmp
                                        val updatedGroups = departments[d2].groups.toMutableList()
                                        updatedGroups[g2] = departments[d2].groups[g2].copy(name = editGroupName, leaderName = editGroupLeader, employees = updatedEmps)
                                        departments[d2] = departments[d2].copy(groups = updatedGroups)
                                    }
                                }
                            } else {
                                val gIdx = departments[dIdx].groups.indexOfFirst { it.id == group.id }
                                if (gIdx >= 0) {
                                    val updatedGroups = departments[dIdx].groups.toMutableList()
                                    updatedGroups[gIdx] = group.copy(name = editGroupName, leaderName = editGroupLeader)
                                    departments[dIdx] = departments[dIdx].copy(groups = updatedGroups)
                                }
                            }
                        }
                        editingGroupInfo = null
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) { Text("确认移入") }
                },
                dismissButton = { TextButton(onClick = { showMoveConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }
    }

    // ===== Edit Employee Dialog =====
    editingEmpInfo?.let { (deptId, groupId, emp) ->
        var editRole by remember(emp.id) { mutableStateOf(emp.role) }
        var editDeptId by remember(emp.id) { mutableStateOf(deptId) }
        var editGroupId by remember(emp.id) { mutableStateOf(groupId) }
        var roleExpanded by remember { mutableStateOf(false) }
        var deptExpanded by remember { mutableStateOf(false) }
        var groupExpanded by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showRestoreConfirm by remember { mutableStateOf(false) }
        var showMoveConfirm by remember { mutableStateOf(false) }
        val roleOptions= listOf("超级管理员", "高级管理层", "部门负责人", "执行人员")
        val editDeptGroups = departments.find { it.id == editDeptId }?.groups?.filter { it.isActive } ?: emptyList()

        // Handle stale groupId when department changes
        if (editGroupId > 0 && editDeptGroups.none { it.id == editGroupId }) {
            editGroupId = editDeptGroups.firstOrNull()?.id ?: 0L
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("停用员工", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = { Text("确定停用「${emp.name}」？停用后该员工将无法登录系统。", fontSize = 14.sp, color = textPrimary) },
                confirmButton = {
                    Button(onClick = {
                        val dIdx = departments.indexOfFirst { it.id == editDeptId }
                        if (dIdx >= 0) {
                            val gIdx = departments[dIdx].groups.indexOfFirst { it.id == editGroupId }
                            if (gIdx >= 0) {
                                val g = departments[dIdx].groups[gIdx]
                                val eIdx = g.employees.indexOfFirst { it.id == emp.id }
                                if (eIdx >= 0) {
                                    val updatedEmps = g.employees.toMutableList().apply { set(eIdx, emp.copy(isActive = false)) }
                                    val updatedGroups = departments[dIdx].groups.toMutableList().apply { set(gIdx, g.copy(employees = updatedEmps)) }
                                    departments[dIdx] = departments[dIdx].copy(groups = updatedGroups)
                                }
                            }
                        }
                        editingEmpInfo = null
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = colorDanger)
                    ) { Text("确认停用", color = Color.White) }
                },
                dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }

        if (showRestoreConfirm) {
            AlertDialog(
                onDismissRequest = { showRestoreConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("恢复使用", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = { Text("确定恢复「${emp.name}」的账号？恢复后该员工可以正常登录系统。", fontSize = 14.sp, color = textPrimary) },
                confirmButton = {
                    Button(onClick = {
                        val dIdx = departments.indexOfFirst { it.id == editDeptId }
                        if (dIdx >= 0) {
                            val gIdx = departments[dIdx].groups.indexOfFirst { it.id == editGroupId }
                            if (gIdx >= 0) {
                                val g = departments[dIdx].groups[gIdx]
                                val eIdx = g.employees.indexOfFirst { it.id == emp.id }
                                if (eIdx >= 0) {
                                    val updatedEmps = g.employees.toMutableList().apply { set(eIdx, emp.copy(isActive = true)) }
                                    val updatedGroups = departments[dIdx].groups.toMutableList().apply { set(gIdx, g.copy(employees = updatedEmps)) }
                                    departments[dIdx] = departments[dIdx].copy(groups = updatedGroups)
                                }
                            }
                        }
                        editingEmpInfo = null
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = colorSuccess)
                    ) { Text("确认恢复", color = Color.White) }
                },
                dismissButton = { TextButton(onClick = { showRestoreConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }

        AlertDialog(
            onDismissRequest = { editingEmpInfo = null },
            shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
            title = { Text("编辑员工", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("姓名：${emp.name}", fontSize = 14.sp, color = textPrimary)
                    ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = it }) {
                        OutlinedTextField(value = editRole, onValueChange = {}, readOnly = true, label = { Text("角色") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                            roleOptions.forEach { r -> DropdownMenuItem(text = { Text(r, fontSize = 13.sp) }, onClick = { editRole = r; roleExpanded = false }) }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                        OutlinedTextField(value = departments.find { it.id == editDeptId }?.name ?: "", onValueChange = {}, readOnly = true, label = { Text("所属部门") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                            departments.forEach { dept -> DropdownMenuItem(text = { Text(dept.name, fontSize = 13.sp) }, onClick = { editDeptId = dept.id; deptExpanded = false; editGroupId = 0L }) }
                        }
                    }
                    if (editDeptGroups.isNotEmpty()) {
                        ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = it }) {
                            OutlinedTextField(value = editDeptGroups.find { it.id == editGroupId }?.name ?: "", onValueChange = {}, readOnly = true, label = { Text("所在分组") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                            )
                            ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                                editDeptGroups.forEach { g -> DropdownMenuItem(text = { Text(g.name, fontSize = 13.sp) }, onClick = { editGroupId = g.id; groupExpanded = false }) }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    if (emp.isActive) {
                        TextButton(onClick = { showDeleteConfirm = true }, colors = ButtonDefaults.textButtonColors(contentColor = colorDanger)) {
                            Icon(Icons.Filled.RemoveCircleOutline, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("停用此员工", fontSize = 13.sp)
                        }
                    } else {
                        TextButton(onClick = { showRestoreConfirm = true }, colors = ButtonDefaults.textButtonColors(contentColor = colorSuccess)) {
                            Icon(Icons.Filled.RestartAlt, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("恢复使用", fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editDeptId != deptId || editGroupId != groupId) {
                        showMoveConfirm = true
                    } else {
                        val newDeptIdx = departments.indexOfFirst { it.id == editDeptId }
                        if (newDeptIdx >= 0) {
                            val newGIdx = departments[newDeptIdx].groups.indexOfFirst { it.id == editGroupId }
                            if (newGIdx >= 0) {
                                val updatedEmps = departments[newDeptIdx].groups[newGIdx].employees.filter { it.id != emp.id } + emp.copy(role = editRole)
                                val updatedGroups = departments[newDeptIdx].groups.toMutableList().apply {
                                    set(newGIdx, departments[newDeptIdx].groups[newGIdx].copy(employees = updatedEmps))
                                }
                                departments[newDeptIdx] = departments[newDeptIdx].copy(groups = updatedGroups)
                            }
                        }
                        editingEmpInfo = null
                    }
                }, enabled = editGroupId > 0, shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = role.primary),
                ) { Text("保存") }
            },
            dismissButton = { TextButton(onClick = { editingEmpInfo = null }) { Text("取消", color = textSecondary) } },
        )

        if (showMoveConfirm) {
            val targetDeptName = departments.find { it.id == editDeptId }?.name ?: ""
            val targetGroupName = departments.find { it.id == editDeptId }?.groups?.find { it.id == editGroupId }?.name ?: ""
            AlertDialog(
                onDismissRequest = { showMoveConfirm = false },
                shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
                title = { Text("移出确认", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
                text = { Text("确定将「${emp.name}」移动到「${targetDeptName} - ${targetGroupName}」？", fontSize = 14.sp, color = textPrimary) },
                confirmButton = {
                    Button(onClick = {
                        removeEmployeeFromDept(departments, emp.id)
                        val newDeptIdx = departments.indexOfFirst { it.id == editDeptId }
                        if (newDeptIdx >= 0) {
                            val newGIdx = departments[newDeptIdx].groups.indexOfFirst { it.id == editGroupId }
                            if (newGIdx >= 0) {
                                val updatedEmps = departments[newDeptIdx].groups[newGIdx].employees.filter { it.id != emp.id } + emp.copy(role = editRole)
                                val updatedGroups = departments[newDeptIdx].groups.toMutableList().apply {
                                    set(newGIdx, departments[newDeptIdx].groups[newGIdx].copy(employees = updatedEmps))
                                }
                                departments[newDeptIdx] = departments[newDeptIdx].copy(groups = updatedGroups)
                            }
                        }
                        editingEmpInfo = null
                    }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                    ) { Text("确认移入") }
                },
                dismissButton = { TextButton(onClick = { showMoveConfirm = false }) { Text("取消", color = textSecondary) } },
            )
        }
    }
}

// ===== Search bar =====

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    role: RoleColor,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.White,
        border = BorderStroke(0.67.dp, colorBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = textMuted,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 13.sp,
                    color = textPrimary,
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                "搜索部门 / 分组 / 员工",
                                fontSize = 13.sp,
                                color = textPrimary.copy(alpha = 0.4f),
                            )
                        }
                        innerTextField()
                    }
                },
                cursorBrush = SolidColor(role.primary),
            )
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp),
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "清除",
                        tint = textMuted,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

// ===== Department card =====

@Composable
private fun DepartmentCard(
    department: Department,
    isExpanded: Boolean,
    expandedGroups: MutableMap<Long, Boolean>,
    searchQuery: String,
    isSearching: Boolean,
    onToggleDept: () -> Unit,
    onToggleGroup: (Long) -> Unit,
    onEditDept: () -> Unit,
    onEditGroup: (Long, Group) -> Unit,
    onEditEmployee: (Long, Long, Employee) -> Unit,
    role: RoleColor,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(0.67.dp, colorSoftBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onToggleDept,
                        onLongClick = onEditDept,
                    ),
                color = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Icon
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = role.primary.copy(alpha = 0.082f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Groups,
                                contentDescription = null,
                                tint = role.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    // Name + leader
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            department.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "负责人 ${department.leaderName} · ${department.groups.sumOf { it.employees.size }} 人",
                            fontSize = 11.sp,
                            color = textSecondary,
                        )
                    }

                    // Status badge
                    val badgeColor = if (department.isActive) colorSuccess else colorDanger
                    val badgeText = if (department.isActive) "正常" else "停用"
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.12f),
                    ) {
                        Text(
                            badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Expand icon
                    Icon(
                        if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = textMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Groups and employees (animated expand/collapse)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // Department leader card with themed border & background
                    val deptLeader = department.groups.flatMap { it.employees }.find { it.name == department.leaderName }
                    if (deptLeader != null) {
                        val leaderGroupId = department.groups.find { it.employees.any { e -> e.name == department.leaderName } }?.id ?: 0L
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(role.primary.copy(alpha = 0.06f))
                                .border(0.5.dp, role.primary.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        ) {
                            EmployeeRow(
                                employee = deptLeader,
                                deptIsActive = department.isActive,
                                onEdit = { onEditEmployee(department.id, leaderGroupId, deptLeader) },
                                role = role,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))

                    val displayGroups = department.groups.filter { group ->
                        if (!isSearching) true
                        else group.name.contains(searchQuery) ||
                                group.employees.any { it.name.contains(searchQuery) }
                    }
                    displayGroups.forEach { group ->
                        GroupCard(
                            group = group,
                            deptIsActive = department.isActive,
                            deptLeaderName = department.leaderName,
                            isExpanded = expandedGroups[group.id] ?: isSearching,
                            searchQuery = searchQuery,
                            isSearching = isSearching,
                            onToggle = { onToggleGroup(group.id) },
                            onEditGroup = { onEditGroup(department.id, group) },
                            onEditEmployee = { emp -> onEditEmployee(department.id, group.id, emp) },
                            role = role,
                        )
                    }
                }
            }
        }
    }
}

// ===== Group card =====

@Composable
private fun GroupCard(
    group: Group,
    deptIsActive: Boolean,
    deptLeaderName: String,
    isExpanded: Boolean,
    searchQuery: String,
    isSearching: Boolean,
    onToggle: () -> Unit,
    onEditGroup: () -> Unit,
    onEditEmployee: (Employee) -> Unit,
    role: RoleColor,
) {
    val active = group.isActive && deptIsActive
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = surfaceWhite,
        border = BorderStroke(0.5.dp, colorBorder.copy(alpha = 0.6f)),
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onToggle,
                        onLongClick = onEditGroup,
                    ),
                color = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Folder,
                        contentDescription = null,
                        tint = if (active) colorInfo else textMuted,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        group.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (active) textPrimary else textMuted,
                        modifier = Modifier.weight(1f),
                    )

                    Text(
                        "组长 ${group.leaderName} · ${group.employees.size} 人",
                        fontSize = 11.sp,
                        color = textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    Icon(
                        if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // Employee rows
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val displayEmployees = group.employees
                        .filter { emp ->
                            if (emp.name == deptLeaderName) false // department leader shown separately
                            else if (!isSearching) true
                            else emp.name.contains(searchQuery) || emp.role.contains(searchQuery)
                        }
                        .sortedBy { if (it.name == group.leaderName) 0 else 1 }
                    displayEmployees.forEach { emp ->
                        EmployeeRow(employee = emp, deptIsActive = active, onEdit = { onEditEmployee(emp) }, role = role)
                    }
                }
            }
        }
    }
}

// ===== Employee row =====

@Composable
private fun EmployeeRow(
    employee: Employee,
    deptIsActive: Boolean,
    onEdit: () -> Unit = {},
    role: RoleColor,
) {
    val active = employee.isActive && deptIsActive
    val badgeColor = when (employee.role) {
        "超级管理员" -> RoleColors.superadmin.primary
        "高级管理层" -> RoleColors.president.primary
        "部门负责人" -> RoleColors.manager.primary
        else -> RoleColors.staff.primary
    }
    val statusColor = if (active) colorSuccess else colorDanger
    val statusText = if (active) "正常" else "停用"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onEdit,
            ),
        shape = RoundedCornerShape(8.dp),
        color = pageBg,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = badgeColor,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        employee.name.first().toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))

            // Name + title
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    employee.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (active) textPrimary else textMuted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        employee.title,
                        fontSize = 11.sp,
                        color = textSecondary,
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.10f),
                    ) {
                        Text(
                            employee.role,
                            fontSize = 9.sp,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        )
                    }
                }
            }

            // Status badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusColor.copy(alpha = 0.10f),
            ) {
                Text(
                    statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}

// ===== New Department Dialog =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewDepartmentDialog(
    departments: List<Department>,
    existingNames: Set<String>,
    allEmployees: List<Employee>,
    role: RoleColor,
    onDismiss: () -> Unit,
    onConfirm: (name: String, leaderName: String, groups: List<EmpGroupInput>) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedLeaderName by remember { mutableStateOf("") }
    var leaderExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var showLeaderConfirm by remember { mutableStateOf(false) }
    var pendingLeaderName by remember { mutableStateOf("") }
    var groups by remember { mutableStateOf(listOf(EmpGroupInput("默认分组"))) }

    val activeEmployees = allEmployees.filter { it.isActive }
    val canSubmit = name.isNotBlank() && nameError == null && groups.isNotEmpty() && groups.all { it.name.isNotBlank() }

    // Leader move confirmation
    if (showLeaderConfirm) {
        AlertDialog(
            onDismissRequest = { showLeaderConfirm = false },
            shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
            title = { Text("移出确认", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, null, tint = Color(0xFFE6A817), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("$pendingLeaderName 将从原部门移出到新部门", fontSize = 13.sp, color = textPrimary)
                }
            },
            confirmButton = { Button(onClick = { selectedLeaderName = pendingLeaderName; showLeaderConfirm = false },
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = role.primary)
            ) { Text("确认移出") } },
            dismissButton = { TextButton(onClick = { showLeaderConfirm = false }) { Text("取消", color = textSecondary) } },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
        title = { Text("新建部门", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Department name
                OutlinedTextField(
                    value = name, onValueChange = {
                        name = it; nameError = when { it.isBlank() -> null; it in existingNames -> "同名部门已存在"; else -> null }
                    },
                    label = { Text("部门名称 *") }, isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = colorDanger, fontSize = 11.sp) } },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary, errorBorderColor = colorDanger, errorLabelColor = colorDanger),
                )
                // Leader dropdown
                ExposedDropdownMenuBox(expanded = leaderExpanded, onExpandedChange = { leaderExpanded = it }) {
                    OutlinedTextField(
                        value = selectedLeaderName, onValueChange = {}, readOnly = true,
                        label = { Text("负责人（可选）") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenu(expanded = leaderExpanded, onDismissRequest = { leaderExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("（不指定）", fontSize = 13.sp, color = textSecondary) },
                            onClick = { selectedLeaderName = ""; leaderExpanded = false },
                        )
                        activeEmployees.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text(employeeLocationLabel(departments, emp), fontSize = 13.sp) },
                                onClick = { pendingLeaderName = emp.name; leaderExpanded = false; showLeaderConfirm = true },
                            )
                        }
                    }
                }
                // Groups section
                Text("分组设置", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                groups.forEachIndexed { idx, group ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = group.name,
                            onValueChange = { groups = groups.toMutableList().apply { set(idx, group.copy(name = it)) } },
                            label = { Text("分组名称 *") }, singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        if (groups.size > 1) {
                            IconButton(onClick = { groups = groups.toMutableList().apply { removeAt(idx) } }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.RemoveCircleOutline, "删除", tint = colorDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                TextButton(onClick = { groups = groups + EmpGroupInput("") }) {
                    Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("添加分组", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedLeaderName, groups) },
                enabled = canSubmit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary),
            ) { Text("确定创建") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消", color = textSecondary) } },
    )
}

// ===== New Employee Dialog =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewEmployeeDialog(
    departments: List<Department>,
    allEmployees: List<Employee>,
    existingNames: Set<String>,
    existingPhones: Set<String>,
    role: RoleColor,
    onDismiss: () -> Unit,
    onConfirm: (name: String, roleName: String, phone: String, title: String, password: String, deptId: Long, groupId: Long) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("执行人员") }
    var phone by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedDeptId by remember { mutableStateOf(departments.firstOrNull()?.id ?: 0L) }
    var selectedGroupId by remember { mutableStateOf(0L) }
    var roleExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    var groupExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val roleOptions = listOf("超级管理员", "高级管理层", "部门负责人", "执行人员")
    val normalizedExistingPhones = remember { existingPhones.map { it.filter { c -> c.isDigit() } }.toSet() }

    val currentDeptGroups = departments.find { it.id == selectedDeptId }?.groups?.filter { it.isActive } ?: emptyList()
    if (selectedGroupId > 0 && currentDeptGroups.none { it.id == selectedGroupId }) {
        selectedGroupId = currentDeptGroups.firstOrNull()?.id ?: 0L
    }

    val rawDigits = phone.filter { it.isDigit() }
    val canSubmit = name.isNotBlank() && nameError == null && rawDigits.length == 11 && phoneError == null && selectedGroupId > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
        title = { Text("新增人员", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = {
                        name = it; nameError = when { it.isBlank() -> null; it in existingNames -> "该姓名已存在"; else -> null }
                    },
                    label = { Text("姓名 *") }, isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = colorDanger, fontSize = 11.sp) } },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary, errorBorderColor = colorDanger, errorLabelColor = colorDanger),
                )
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                )
                ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = it }) {
                    OutlinedTextField(
                        value = selectedRole, onValueChange = {}, readOnly = true,
                        label = { Text("角色") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                        roleOptions.forEach { r ->
                            DropdownMenuItem(text = { Text(r, fontSize = 13.sp) },
                                onClick = { selectedRole = r; roleExpanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = phone, onValueChange = {
                        phone = it; val digits = it.filter { c -> c.isDigit() }
                        phoneError = when {
                            digits.length > 11 -> "手机号不能超过11位"
                            digits.length == 11 -> {
                                when {
                                    digits[0] != '1' || digits[1] !in '3'..'9' -> "手机号格式不正确"
                                    digits in normalizedExistingPhones -> "该手机号已被其他员工使用"
                                    else -> null
                                }
                            }
                            else -> null
                        }
                    },
                    label = { Text("手机号 *") }, isError = phoneError != null,
                    supportingText = phoneError?.let { { Text(it, color = colorDanger, fontSize = 11.sp) } },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary, errorBorderColor = colorDanger, errorLabelColor = colorDanger),
                )
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("职位") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                )
                ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                    val deptName = departments.find { it.id == selectedDeptId }?.name ?: ""
                    OutlinedTextField(
                        value = deptName, onValueChange = {}, readOnly = true,
                        label = { Text("所属部门") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                        departments.forEach { dept ->
                            DropdownMenuItem(text = { Text(dept.name, fontSize = 13.sp) },
                                onClick = { selectedDeptId = dept.id; deptExpanded = false; selectedGroupId = 0L })
                        }
                    }
                }
                if (currentDeptGroups.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = it }) {
                        val groupName = currentDeptGroups.find { it.id == selectedGroupId }?.name ?: ""
                        OutlinedTextField(
                            value = groupName, onValueChange = {}, readOnly = true,
                            label = { Text("所在分组") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                        )
                        ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                            currentDeptGroups.forEach { group ->
                                DropdownMenuItem(text = { Text(group.name, fontSize = 13.sp) },
                                    onClick = { selectedGroupId = group.id; groupExpanded = false })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedRole, rawDigits, title, password, selectedDeptId, selectedGroupId) },
                enabled = canSubmit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary),
            ) { Text("确定添加") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消", color = textSecondary) } },
    )
}

// ===== New Group Dialog =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewGroupDialog(
    departments: List<Department>,
    role: RoleColor,
    onDismiss: () -> Unit,
    onConfirm: (groupName: String, leaderName: String, deptId: Long) -> Unit,
) {
    var groupName by remember { mutableStateOf("") }
    var selectedDeptId by remember { mutableStateOf(departments.firstOrNull()?.id ?: 0L) }
    var selectedLeaderName by remember { mutableStateOf("") }
    var deptExpanded by remember { mutableStateOf(false) }
    var leaderExpanded by remember { mutableStateOf(false) }
    var showLeaderConfirm by remember { mutableStateOf(false) }
    var pendingLeaderName by remember { mutableStateOf("") }

    val allEmps = allEmployeesList(departments)
    val activeEmps = allEmps.filter { it.isActive }

    if (showLeaderConfirm) {
        AlertDialog(
            onDismissRequest = { showLeaderConfirm = false },
            shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
            title = { Text("移出确认", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, null, tint = Color(0xFFE6A817), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("$pendingLeaderName 将从原部门移出到${departments.find { it.id == selectedDeptId }?.name ?: ""}", fontSize = 13.sp, color = textPrimary)
                }
            },
            confirmButton = {
                Button(onClick = { selectedLeaderName = pendingLeaderName; showLeaderConfirm = false },
                    shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = role.primary)
                ) { Text("确认移出") }
            },
            dismissButton = { TextButton(onClick = { showLeaderConfirm = false }) { Text("取消", color = textSecondary) } },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp), containerColor = surfaceWhite,
        title = { Text("新建分组", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = groupName, onValueChange = { groupName = it },
                    label = { Text("分组名称 *") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                )
                ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                    OutlinedTextField(
                        value = departments.find { it.id == selectedDeptId }?.name ?: "", onValueChange = {},
                        readOnly = true, label = { Text("所属部门") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                        departments.forEach { dept ->
                            DropdownMenuItem(text = { Text(dept.name, fontSize = 13.sp) },
                                onClick = { selectedDeptId = dept.id; deptExpanded = false })
                        }
                    }
                }
                ExposedDropdownMenuBox(expanded = leaderExpanded, onExpandedChange = { leaderExpanded = it }) {
                    OutlinedTextField(
                        value = selectedLeaderName, onValueChange = {},
                        readOnly = true, label = { Text("组长（可选）") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = role.primary, focusedLabelColor = role.primary),
                    )
                    ExposedDropdownMenu(expanded = leaderExpanded, onDismissRequest = { leaderExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("（不指定）", fontSize = 13.sp, color = textSecondary) },
                            onClick = { selectedLeaderName = ""; leaderExpanded = false },
                        )
                        activeEmps.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text(employeeLocationLabel(departments, emp), fontSize = 13.sp) },
                                onClick = { pendingLeaderName = emp.name; leaderExpanded = false; showLeaderConfirm = true },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(groupName, selectedLeaderName, selectedDeptId) },
                enabled = groupName.isNotBlank() && selectedDeptId > 0,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = role.primary),
            ) { Text("确定创建") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消", color = textSecondary) } },
    )
}

// ===== Preview =====

@Composable
private fun SpeedDialItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = LocalRoleColors.current.primary,
        shadowElevation = 6.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Spacer(Modifier.width(8.dp))
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun OrgManagementScreenPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        OrgManagementScreen(onBack = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun OrgManagementScreenLoadingPreview() {
    MeetingTheme(roleColors = RoleColors.superadmin) {
        Box(modifier = Modifier.background(pageBg)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(50.dp), color = colorSkeleton) {}
                Surface(modifier = Modifier.fillMaxWidth().height(18.dp), shape = RoundedCornerShape(4.dp), color = colorSkeleton) {}
                repeat(3) {
                    Surface(modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(14.dp), color = colorSkeleton) {}
                }
            }
        }
    }
}
