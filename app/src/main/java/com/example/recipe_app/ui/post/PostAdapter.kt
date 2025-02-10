
package com.example.recipe_app.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_app.R
import com.example.recipe_app.Data.model.Post
import com.example.recipe_app.Data.model.User
import com.example.recipe_app.ui.addPost.EditPostFragment
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostAdapter(private val onDeletePost: (Post) -> Unit) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {
    private var currentUser: User? = null

    fun setCurrentUser(user: User?) {
        this.currentUser = user
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.postTitle)
        private val ingredientsListLayout: LinearLayout = itemView.findViewById(R.id.ingredientsList)
        private val expandIngredientsButton: Button = itemView.findViewById(R.id.expandIngredientsButton)
        private val preparationTextView: TextView = itemView.findViewById(R.id.postPreparation)
        private val preparationTimeTextView: TextView = itemView.findViewById(R.id.postPreparationTime)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.postCreatedAt)
        private val updatedAtTextView: TextView = itemView.findViewById(R.id.postUpdatedAt)
        private val postImageView: ImageView = itemView.findViewById(R.id.postImage)
        private val expandCollapseButton: Button = itemView.findViewById(R.id.expandCollapseButton)
        private val editButton: Button = itemView.findViewById(R.id.editPostButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deletePostButton)


        private var isExpanded = false
        private var isIngredientsExpanded = false

        fun bind(post: Post) {
            titleTextView.text = post.title

            ingredientsListLayout.removeAllViews()
            val maxVisibleIngredients = 3
            post.ingredients.forEachIndexed { index, ingredient ->
                val ingredientTextView = TextView(itemView.context).apply {
                    text = "${ingredient.name}: ${ingredient.quantity} ${ingredient.unit}"
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    visibility = if (index < maxVisibleIngredients || isIngredientsExpanded) View.VISIBLE else View.GONE
                }
                ingredientsListLayout.addView(ingredientTextView)
            }

            expandIngredientsButton.visibility =
                if (post.ingredients.size > maxVisibleIngredients) View.VISIBLE else View.GONE

            expandIngredientsButton.setOnClickListener {
                isIngredientsExpanded = !isIngredientsExpanded
                updateIngredientsVisibility()
            }

            preparationTextView.text = post.preparation
            preparationTimeTextView.text = "${post.preparationTime} minutes"
            createdAtTextView.text = "Created At: ${formatDate(post.createdAt)}"
            updatedAtTextView.text = "Updated At: ${formatDate(post.updatedAt)}"

            post.imageUrl?.let { Picasso.get().load(it).into(postImageView) }

            setPreparationTextVisibility()

            expandCollapseButton.setOnClickListener {
                isExpanded = !isExpanded
                setPreparationTextVisibility()
            }

            preparationTextView.post {
                val shouldShowButton = preparationTextView.lineCount > 3 || post.preparation.length > 150
                expandCollapseButton.visibility = if (shouldShowButton) View.VISIBLE else View.GONE
                preparationTextView.maxLines = if (shouldShowButton) 3 else Int.MAX_VALUE
            }

            currentUser?.let { user ->
                if (post.userId == user.uid) {
                    editButton.visibility = View.VISIBLE
                    deleteButton.visibility = View.VISIBLE
                } else {
                    editButton.visibility = View.GONE
                    deleteButton.visibility = View.GONE
                }
            }

            editButton.setOnClickListener {
                val context = itemView.context

                if (context is AppCompatActivity) {
                    val fragmentTransaction = context.supportFragmentManager.beginTransaction()

                    val editPostFragment = EditPostFragment().apply {
                        arguments = Bundle().apply {
                            putString("POST_ID", post.id)
                        }
                    }

                    fragmentTransaction.replace(R.id.fragment_container, editPostFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            deleteButton.setOnClickListener {
                onDeletePost(post)
            }
        }


        private fun updateIngredientsVisibility() {
            for (i in 0 until ingredientsListLayout.childCount) {
                val ingredientView = ingredientsListLayout.getChildAt(i)
                ingredientView.visibility =
                    if (isIngredientsExpanded || i < 3) View.VISIBLE else View.GONE
            }
        }

        private fun setPreparationTextVisibility() {
            preparationTextView.maxLines = if (isExpanded) Int.MAX_VALUE else 3
        }

        private fun formatDate(timestamp: Long?): String {
            return timestamp?.let {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                dateFormat.format(Date(it))
            } ?: "N/A"
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
