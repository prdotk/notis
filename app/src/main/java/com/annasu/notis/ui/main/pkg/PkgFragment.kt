package com.annasu.notis.ui.main.pkg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.annasu.notis.R
import com.annasu.notis.databinding.MainPkgFragmentBinding
import com.annasu.notis.extension.dp2Pixel
import com.annasu.notis.ui.custom.LinearLayoutItemDecoration
import com.annasu.notis.ui.summary.SummaryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PkgFragment : Fragment() {

    private val viewModel: PkgViewModel by viewModels()

    private lateinit var binding: MainPkgFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_pkg_fragment, container, false)
        binding.lifecycleOwner = this

        val adapter = PkgAdapter {
            val intent = Intent(context, SummaryActivity::class.java)
            intent.putExtra("PKG_NAME", it)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.pkgInfoWithNotiRecentViews.collectLatest {
                adapter.submitData(lifecycle, it)
            }
        }
        
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(LinearLayoutItemDecoration(
            requireContext().dp2Pixel(5f), requireContext().dp2Pixel(20f)))

        return binding.root
    }
}