package com.app.workreport.ui.dashboard.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.workreport.model.LeaveData
import com.app.workreport.network.ApiService
import com.app.workreport.util.*
import retrofit2.HttpException
import java.io.IOException

class LeaveListPagingSource(private val pageNo:Int, private val apiService:ApiService) :
            PagingSource<Int, LeaveData>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaveData> {
                val page = params.key ?: pageNo
                return try {
                    val map = mutableMapOf<String, Any>()
                    map[PAGE] = "$page"
                    map[COUNT] = 10
                    map[VIEW_TYPE] = LIST
                    map[LOCALE] = getLocale()
                    map[WORKER_ID] = AppPref.userID ?: ""
                    val response = apiService.getAllLeaveList(map)
                    if (response.isSuccessful) {
                        if (response.body() != null && response.body()!!.data != null) {
                            LoadResult.Page(
                                response.body()?.data!!.data,
                                prevKey = if (page == 1) null else page - 1,
                                nextKey = if (response.body()!!.data == null || response.body()!!.data!!.data.isEmpty()) null else page + 1
                            )
                        } else {
                            return LoadResult.Error(Exception(response.message()))
                        }
                    } else {
                        return LoadResult.Error(Exception(response.message()))

                    }
                } catch (exception: IOException) {
                    return LoadResult.Error(exception)
                } catch (exception: HttpException) {
                    return LoadResult.Error(exception)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, LeaveData>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPageIndex =
                        state.pages.indexOf(state.closestPageToPosition(anchorPosition))
                    state.pages.getOrNull(anchorPageIndex + 1)?.prevKey ?: state.pages.getOrNull(
                        anchorPageIndex - 1
                    )?.nextKey

                }

/*
            override fun getRefreshKey(state: PagingState<Int, DataTask>): Int? {
              //  TODO("Not yet implemented")

                return null
            }
*/
            }
        }
