package com.enterprise.meeting.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.meeting.domain.model.Memo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeStats(
    val pendingReviewCount: Int = 0,
    val pendingConfirmCount: Int = 0,
    val overdueCount: Int = 0,
    val employeeCount: Int = 0,
    val departmentCount: Int = 0,
    val activeAccountCount: Int = 0,
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val userInitial: String,
        val userDisplayName: String,
        val userRole: String,
        val companyName: String,
        val appTitle: String,
        val stats: HomeStats,
        val memoPreview: Memo?,
    ) : HomeUiState
    data class Error(val message: String = "加载失败") : HomeUiState
    data object Empty : HomeUiState
}

/**
 * 首页 ViewModel
 *
 * 当前使用 mock 数据用于 UI 开发和验证。
 * 数据层就绪后，通过 Hilt 注入 [AuthRepository]、[MeetingRepository]、
 * [TaskRepository]、[MemoRepository] 替换 [loadMockData] 中的硬编码数据。
 *
 * 注入仓库后需同时在 di/ 模块中添加对应的 @Provides 或 @Binds 方法。
 *
 * 仓库接口定义在 domain/repository/ 包中。
 * API 客户端定义在 data/remote/api/ 包中（已提供 Hilt 绑定）。
 * DAO 定义在 data/local/dao/ 包中（已提供 Hilt 绑定）。
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // 模拟网络加载延迟，展示骨架屏
            delay(600)

            loadMockData()
        }
    }

    private fun loadMockData() {
        val memo = Memo(
            id = 1,
            title = "下午 3 点确认市场活动场地",
            content = null,
            reminderAt = null,
            isCompleted = false,
            color = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )

        _uiState.value = HomeUiState.Success(
            userInitial = "张",
            userDisplayName = "张伟",
            userRole = "superadmin",
            companyName = "远航科技集团",
            appTitle = "企业会议中枢",
            stats = HomeStats(
                pendingReviewCount = 8,
                pendingConfirmCount = 23,
                overdueCount = 6,
                employeeCount = 248,
                departmentCount = 12,
                activeAccountCount = 231,
            ),
            memoPreview = memo,
        )
    }
}
