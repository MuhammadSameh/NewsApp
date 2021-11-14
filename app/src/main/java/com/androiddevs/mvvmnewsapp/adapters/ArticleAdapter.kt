package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

class ArticleAdapter(): RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {


    inner class ArticleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    val differCallback = object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview , parent,false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = differ.currentList[position]
        holder.itemView.apply {
            tvDescription.text = currentArticle.description
            tvPublishedAt.text = currentArticle.publishedAt
            tvSource.text = currentArticle.source?.name
            tvTitle.text = currentArticle.title
            Glide.with(this).load(currentArticle.urlToImage).into(ivArticleImage)
            setOnClickListener {
                onArticleClickListener?.let {
                    it(currentArticle)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onArticleClickListener: ((Article) -> Unit)? = null

    fun setOnClickListener (listener: ((Article) -> Unit)?){
        onArticleClickListener = listener
    }
}