package com.mcmouse88.basic_testing.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mcmouse88.basic_testing.R
import com.mcmouse88.basic_testing.TodoApplication
import com.mcmouse88.basic_testing.databinding.StatisticsFragmentBinding
import com.mcmouse88.basic_testing.util.setupRefreshLayout

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment(R.layout.statistics_fragment) {

    private lateinit var viewDataBinding: StatisticsFragmentBinding

    private val viewModel by viewModels<StatisticsViewModel> {
        StatisticsViewModel.StatisticsViewModelFactory(
            (requireContext().applicationContext as TodoApplication).taskRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding = StatisticsFragmentBinding.bind(view)
        viewDataBinding.viewmodel = viewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }
}
