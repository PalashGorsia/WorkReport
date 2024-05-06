package com.app.workreport.ui.dashboard.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.workreport.model.DataTask
import com.app.workreport.network.ApiService
import com.app.workreport.util.*
import retrofit2.HttpException
import java.io.IOException

class TaskListPagingSource(private val mapD: Map<String, String>,private val apiService:ApiService) :
            PagingSource<Int, DataTask>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataTask> {
                //for first case it will be null, then we can pass some default value, in our case it's 1
                val page = params.key ?: 1
                return try {
                    val map = mutableMapOf<String, Any>()
                    map[PAGE] = "$page"
                    map[COUNT] = PAGE_COUNT
                    map[LOCALE] = AppPref.local ?: ""
                    val dd = mapD[SEARCH]
                    if (dd.toString().isNotEmpty()){
                        map[SEARCH] = dd.toString()
                    }
                    val sp =mapD[STATUS_PROGRESS]
                    if (sp!!.isNotEmpty()&&sp.toInt()>=0){
                        map[STATUS_PROGRESS] = sp.toInt()
                    }

                    val response = apiService.getAllTaskList(map)
                    if (response.isSuccessful) {
                        if (response.body() != null && response.body()!!.data != null) {
                            var result:ArrayList<DataTask> =ArrayList()
                            val data = response.body()!!.data!!.data
                            if (data.isNullOrEmpty()){
                            }else{
                                result = data as ArrayList<DataTask>
                            }
                            LoadResult.Page(
                                result,
                                prevKey = if (page == 1) null else page - 1,
                                nextKey = if (response.body()!!.data == null || response.body()!!.data!!.data.isNullOrEmpty()) null else page + 1
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

            override fun getRefreshKey(state: PagingState<Int, DataTask>): Int? {
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
