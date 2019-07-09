package com.kuky.demo.wan.android.ui.home

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {
    suspend fun loadPageData(page: Int): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }
}

class HomeArticleDataSource(private val repository: HomeArticleRepository) :
    PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(0)
            data?.let {
                callback.onResult(data, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(params.key)
            data?.let {
                callback.onResult(data, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(params.key)
            data?.let {
                callback.onResult(data, params.key - 1)
            }
        }
    }
}

class HomeArticleDataSourceFactory(private val repository: HomeArticleRepository) :
    DataSource.Factory<Int, ArticleDetail>() {

    override fun create(): DataSource<Int, ArticleDetail> = HomeArticleDataSource(repository)
}

class HomeArticleAdapter : BasePagedListAdapter<ArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_home_article

    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerHomeArticleBinding>) {
        holder.binding.detail = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleDetail>() {
            override fun areItemsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}