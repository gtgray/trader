package tk.atna.tradernet.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import tk.atna.tradernet.R
import tk.atna.tradernet.adapter.QuotesAdapter
import tk.atna.tradernet.databinding.FragmentQuotesBinding
import tk.atna.tradernet.internal.QuotesViewModel
import tk.atna.tradernet.internal.ResultCode
import tk.atna.tradernet.model.Quote
import tk.atna.tradernet.stuff.Log

class QuotesFragment : Fragment() {

    private lateinit var binding: FragmentQuotesBinding // R.layout.fragment_quotes

    private val viewModel: QuotesViewModel by viewModels()

    private lateinit var adapter: QuotesAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        //
        initAdapter()
        pullData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuotesBinding.inflate(inflater, container, false)
        //
        populateViews()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //
        binding.rvQuotes.adapter = null
    }

    private fun populateViews() {
        with(binding) {
            rvQuotes.setHasFixedSize(true)
            rvQuotes.adapter = adapter
            (rvQuotes.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun initAdapter() {
        adapter = QuotesAdapter(requireContext(), object : QuotesAdapter.ItemActionListener {
            override fun onItemClick(item: Quote) {

                Toast.makeText(requireContext(), "Go to details of ${item.latinName}", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun pullData() {
        viewModel.pullQuotes().observe(this, Observer { resource ->

            Log.w("----------------- DATA ${resource.resultCode}")

            when (resource.resultCode) {
                // data is present
                ResultCode.SUCCESS -> adapter.setData(resource.data)
                // can't connect - no data
                ResultCode.CONNECT_ERROR -> adapter.setData(null)
                // connection lost - notify user
                ResultCode.UNKNOWN_ERROR -> Toast.makeText(
                    requireContext(),
                    "Live updates error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}