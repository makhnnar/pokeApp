package com.pedrogomez.pokeapp.pokelist

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.pedrogomez.pokeapp.base.BaseActivity
import com.pedrogomez.pokeapp.pokedetail.PokemonDetailActivity
import com.pedrogomez.pokeapp.pokelist.adapter.PokemonViewHolder
import com.pedrogomez.pokeapp.pokelist.adapter.PokemonsAdapter
import com.pedrogomez.pokeapp.pokelist.models.result.Result
import com.pedrogomez.pokeapp.pokelist.viewmodel.PokeListViewModel
import com.pedrogomez.pokeapp.databinding.ActivityPokemonListBinding
import com.pedrogomez.pokeapp.pokelist.models.PokemonData
import com.pedrogomez.pokeapp.utils.PageScrollListener
import com.pedrogomez.pokeapp.utils.extensions.remove
import com.pedrogomez.pokeapp.utils.extensions.show
import org.koin.android.viewmodel.ext.android.viewModel

class PokemonsListActivity : BaseActivity(),
    PokemonViewHolder.OnClickItemListener{

    private val pokeListViewModel : PokeListViewModel by viewModel()

    private lateinit var binding: ActivityPokemonListBinding

    private lateinit var pokemonsAdapter : PokemonsAdapter

    private var counter : CountDownTimer? = null

    private lateinit var pageScrollListener : PageScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        initObservers()
        initEditTextListeners()
        binding.btnToTop.hide()
        hideKeyboard(binding.etSearchField)
    }

    private fun initEditTextListeners() {
        binding.etSearchField.addTextChangedListener {
            if(counter!=null){
                counter?.cancel()
            }
            counter = object : CountDownTimer(500, 100){
                override fun onTick(millisUntilFinished: Long) {

                }
                /**
                 * Este contador se ejecuta para llamar al endpoint si y solo si el usario
                 * dejo de teclear durante un tiempo mayor a 500ms, y asi evitar multiples
                 * llamadas a backend
                 * */
                override fun onFinish() {
                    pokeListViewModel.findPokemon(
                        it.toString()
                    )
                }

            }.start()
        }
    }

    private fun initRecyclerView() {
        pokemonsAdapter = PokemonsAdapter(this@PokemonsListActivity)
        binding.rvPokeItems.apply{
            adapter = pokemonsAdapter
            layoutManager = GridLayoutManager(this@PokemonsListActivity,3)
        }
        binding.srlContainer.setOnRefreshListener {
            pokeListViewModel.getListOfPokemons()
        }
        pageScrollListener = object : PageScrollListener(
            binding.rvPokeItems.layoutManager as GridLayoutManager
        ){
            override fun onLoadMore(
                currentPage: Int
            ) {
                pokeListViewModel.loadMorePokemonsToList(
                    currentPage
                )
            }

            override fun scrollIsOnTop(isOnTop: Boolean) {
                if(isOnTop){
                    binding.btnToTop.hide()
                }else{
                    binding.btnToTop.show()
                }
            }
        }
        binding.rvPokeItems.addOnScrollListener(
            pageScrollListener
        )
        binding.btnToTop.setOnClickListener {
            binding.rvPokeItems.smoothScrollToPosition(0)
        }
    }

    private fun initObservers(){
        pokeListViewModel.observeData().observe(
            this,
            Observer {
                binding.srlContainer.isRefreshing = false
                when (it) {
                    is Result.Success -> {
                        binding.pbPokesLoading.remove()
                        pokemonsAdapter.setData(
                            it.data
                        )
                    }
                    is Result.LoadingNewContent -> {
                        pageScrollListener.initFields()
                        pokemonsAdapter.clearData()
                        hideKeyboard(binding.etSearchField)
                        binding.pbPokesLoading.show()
                    }
                    is Result.LoadingMoreContent -> {
                        binding.pbPokesLoading.show()
                    }
                    is Result.Error -> {
                        binding.pbPokesLoading.remove()
                    }
                }
            }
        )
    }

    override fun goToBookDetail(data: PokemonData) {
        val intent = Intent(
            this,
            PokemonDetailActivity::class.java
        )
        intent.putExtra(
            PokemonDetailActivity.POKE_DATA,
            data
        )
        startActivity(
            intent
        )
    }
}