package com.enterprise.meeting.presentation.meeting.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.meeting.domain.model.MeetingState
import com.enterprise.meeting.presentation.components.MeetingCard
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.meeting.presentation.theme.*

data class MeetingItemData(
    val id: Long,
    val title: String,
    val department: String,
    val host: String,
    val duration: String,
    val attendeeCount: Int,
    val taskCount: Int,
    val state: MeetingState,
    val isHostedByMe: Boolean = false,
)

private val sampleMeetings = listOf(
    MeetingItemData(1, "Q3 产品规划会议", "产品部", "李明", "58:24", 9, 6, MeetingState.PENDING_REVIEW, isHostedByMe = true),
    MeetingItemData(2, "第四季度跨部门协作项目启动与资源协调会议", "运营部", "王思雨", "01:42:08", 14, 11, MeetingState.PENDING_PUBLISH),
    MeetingItemData(3, "周会", "信息技术部", "张伟", "32:11", 6, 0, MeetingState.AI_PROCESSING),
    MeetingItemData(4, "行政预算复盘", "行政部", "李明", "42:15", 5, 4, MeetingState.ARCHIVED, isHostedByMe = true),
    MeetingItemData(6, "产品上线复盘会", "产品部", "王思雨", "51:09", 7, 3, MeetingState.PENDING),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingListScreen(
    onMeetingClick: (Long, MeetingState) -> Unit,
    onCreateMeeting: () -> Unit,
    onUploadClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    val role = LocalRoleColors.current
    var selectedFilter by remember { mutableStateOf<Any?>(null) }

    data class FilterTab(val key: Any?, val label: String, val count: Int = 0)

    val filterTabs = listOf(FilterTab(null, "全部", sampleMeetings.size)) +
            MeetingState.entries.map { state ->
                FilterTab(state, state.displayName, sampleMeetings.count { it.state == state })
            } +
            listOf(FilterTab("hosted", "我主持的", sampleMeetings.count { it.isHostedByMe }))

    val filteredMeetings = sampleMeetings.filter { item ->
        when (selectedFilter) {
            null -> true
            "hosted" -> item.isHostedByMe
            is MeetingState -> item.state == selectedFilter
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("会议", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "搜索", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceWhite,
                    titleContentColor = textPrimary
                )
            )
        },
        containerColor = pageBg
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filterTabs.forEach { tab ->
                        val isSelected = selectedFilter == tab.key
                        Surface(
                            onClick = { selectedFilter = tab.key },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) role.primary else Color.White,
                            border = if (isSelected) BorderStroke(0.67.dp, role.primary)
                                else BorderStroke(0.67.dp, colorBorder),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp).height(36.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = tab.label,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else textSecondary
                                )
                                if (tab.count > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(17.dp)
                                            .background(
                                                color = if (isSelected) Color.White.copy(alpha = 0.25f)
                                                    else colorDanger.copy(alpha = 0.10f),
                                                shape = RoundedCornerShape(50.dp)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = tab.count.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isSelected) Color.White else colorDanger,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(horizontal = 6.dp)
                                                .offset(y = (-3).dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }



                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredMeetings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("暂无会议", style = MaterialTheme.typography.bodyMedium, color = textMuted)
                            }
                        }
                    } else {
                        items(filteredMeetings, key = { it.id }) { meeting ->
                            MeetingCard(
                                title = meeting.title,
                                department = meeting.department,
                                host = meeting.host,
                                duration = meeting.duration,
                                attendeeCount = meeting.attendeeCount,
                                taskCount = meeting.taskCount,
                                state = meeting.state,
                                modifier = Modifier.animateItem(),
                                onClick = { onMeetingClick(meeting.id, meeting.state) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    onClick = onUploadClick,
                    shape = RoundedCornerShape(22.dp),
                    color = surfaceWhite,
                    border = BorderStroke(1.dp, colorBorder)
                ) {
                    Row(
                        modifier = Modifier.height(44.dp).padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = textPrimary
                        )
                        Text("上传录音", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                    }
                }

                Surface(
                    onClick = onCreateMeeting,
                    shape = RoundedCornerShape(28.dp),
                    color = role.primary
                ) {
                    Row(
                        modifier = Modifier.height(56.dp).padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Text(
                            "开始新会议",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 34)
@Composable
private fun MeetingListScreenPreview() {
    MeetingTheme {
        MeetingListScreen(
            onMeetingClick = { _, _ -> },
            onCreateMeeting = {}
        )
    }
}
