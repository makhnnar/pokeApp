package com.pedrogomez.pokeapp.pokedetail.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.pedrogomez.pokeapp.R
import com.pedrogomez.pokeapp.databinding.TitledStatusFieldBinding
import com.pedrogomez.pokeapp.utils.extensions.getColor

class TitledStatusField : ConstraintLayout {

    lateinit var binding : TitledStatusFieldBinding

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        binding = TitledStatusFieldBinding.inflate(
            LayoutInflater.from(context),
            this
        )
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.TitledStatusField,
                defStyle,
                0
        )

        val titleStatus = a.getString(
            R.styleable.TitledStatusField_tsfTitle
        )

        val titleColor = a.getColor(
            R.styleable.TitledStatusField_tsfColorTitle,
            getColor(R.color.black)
        )

        val valueColor = a.getColor(
            R.styleable.TitledStatusField_tsfColorValue,
            getColor(R.color.black)
        )

        binding.tvTitleStatus.text = titleStatus?:""

        binding.tvTitleStatus.setTextColor(titleColor)
        binding.tvValueStatus.setTextColor(valueColor)

        a.recycle()

    }

    fun setValue(value:Int){
        binding.tvValueStatus.text = "$value"
    }

}