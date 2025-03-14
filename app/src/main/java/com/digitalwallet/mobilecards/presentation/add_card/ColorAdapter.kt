package com.digitalwallet.mobilecards.presentation.add_card

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitalwallet.mobilecards.R

class ColorAdapter(
    private val colorList: List<String>,
    private val onColorSelected: (String) -> Unit,
) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedColor: String? = colorList[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorHex = colorList[position]
        holder.bind(colorHex)
        val colorDrawable = ColorDrawable(Color.parseColor(colorHex))
        val borderDrawable =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_selection)

        val layers = arrayOf(colorDrawable, borderDrawable)
        val layerDrawable = LayerDrawable(layers)

        if (colorHex == selectedColor) {
            holder.colorView.background = layerDrawable
        } else {
            holder.colorView.setBackgroundColor(Color.parseColor(colorHex))
        }
    }

    override fun getItemCount(): Int = colorList.size

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: View = itemView.findViewById(R.id.colorView)

        fun bind(colorHex: String) {
            colorView.setBackgroundColor(Color.parseColor(colorHex))
            itemView.setOnClickListener {
                selectedColor = colorHex
                onColorSelected(colorHex)
                notifyDataSetChanged()
            }
        }
    }
}


