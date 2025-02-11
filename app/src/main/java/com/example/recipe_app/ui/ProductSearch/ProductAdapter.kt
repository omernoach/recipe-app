package com.example.recipe_app.ui.ProductSearch

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipe_app.Data.model.ProductItem
import com.example.recipe_app.databinding.ItemProductBinding

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var items: List<ProductItem> = listOf()

    fun submitList(newItems: List<ProductItem>) {
        Log.d("ProductAdapter", "Submitting list of ${newItems.size} items")
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        Log.d("ProductAdapter", "Binding product at position $position")
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductItem) {
            Log.d("ProductAdapter", "Binding product: ${item.name}")
            binding.nameTextView.text = item.name
            binding.caloriesTextView.text = "Calories: ${item.calories}"
            binding.servingSizeTextView.text = "Serving Size: ${item.serving_size_g}g"
            binding.proteinTextView.text = "Protein: ${item.protein_g}g"
            binding.fatTextView.text = "Fat: ${item.fat_total_g}g"
            binding.fiberTextView.text = "Fiber: ${item.fiber_g}g"
            binding.sugarTextView.text = "Sugar: ${item.sugar_g}g"
            binding.sodiumTextView.text = "Sodium: ${item.sodium_mg}mg"
            binding.potassiumTextView.text = "Potassium: ${item.potassium_mg}mg"
            binding.cholesterolTextView.text = "Cholesterol: ${item.cholesterol_mg}mg"
            binding.carbohydratesTextView.text = "Carbohydrates: ${item.carbohydrates_total_g}g"
        }
    }
}

