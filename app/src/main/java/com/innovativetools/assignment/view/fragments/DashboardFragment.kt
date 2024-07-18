package com.innovativetools.assignment.view.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import com.innovativetools.assignment.R
import com.innovativetools.assignment.view.adapter.LinksAdapter
import com.innovativetools.assignment.databinding.FragmentDahsboardBinding
import com.innovativetools.assignment.data.model.Link
import com.innovativetools.assignment.data.model.RequestBody
import com.innovativetools.assignment.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var chart: LineChart
    private lateinit var selectedStartDate: Calendar
    private lateinit var selectedEndDate: Calendar
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: FragmentDahsboardBinding
//    private val viewModel: DashboardViewModel by viewModels {
//        DashboardViewModelFactory(DashboardRepository(ApiService.apiService))
//    }
    private val viewModel: DashboardViewModel by viewModels()

    private var recentLinksData: List<Link>? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dahsboard, container, false)

        chart = binding.chart
        tabLayout = binding.tabLayout

        viewModel.initialize()

        val requestBody = RequestBody("NewEndPointAdded")
        lifecycleScope.launch {
            viewModel.postData("newEndpoint", requestBody)
        }

        observeViewModel()
        setUpTabs()
        setUpRecentLinkChart()
        setUpDate()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        viewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.tvUserName.text = userName
        }

        viewModel.greeting.observe(viewLifecycleOwner) { greeting ->
            binding.tvGreeting.text = greeting
        }

        viewModel.totalClicks.observe(viewLifecycleOwner) { totalClicks ->
            binding.tvTotalClicksCount.text = totalClicks
        }

        viewModel.todayClicks.observe(viewLifecycleOwner) { todayClicks ->
            binding.tvTodayClicksCount.text = todayClicks
        }

        viewModel.totalLinks.observe(viewLifecycleOwner) { totalLinks ->
            binding.tvTotalLinksCount.text = totalLinks
        }

        viewModel.topLocation.observe(viewLifecycleOwner) { topLocation ->
            binding.tvTopLocName.text = topLocation
        }

        viewModel.topSource.observe(viewLifecycleOwner) { topSource ->
            binding.tvTopSourceName.text = topSource
        }
    }

    private fun setUpTabs() {
        // Adding tabs with custom view
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("Top Links", true)))
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("Recent Links", false)))

        val selectedTabDrawable = resources.getDrawable(R.drawable.fill_btn_round_30, null)
        tabLayout.setSelectedTabIndicator(null)

        // Set default tab background and text color
        val defaultTab = tabLayout.getTabAt(0)
        defaultTab?.select()
        defaultTab?.view?.background = selectedTabDrawable
        displayRecentLinks()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.background = selectedTabDrawable
                val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                textView?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                when (tab.position) {
                    0 -> displayTopLinks()
                    1 -> displayRecentLinks()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.background = null
                val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                textView?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun createTabView(tabText: String, isSelected: Boolean): View {
        val tabView = layoutInflater.inflate(R.layout.custom_tab, null)
        val textView = tabView.findViewById<TextView>(R.id.tab_text)
        textView.text = tabText
        textView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isSelected) android.R.color.white else android.R.color.black
            )
        )
        return tabView
    }


    private fun displayTopLinks() {
        viewModel.topLinks.observe(viewLifecycleOwner) { toplinks ->
            val linksAdapter = LinksAdapter(toplinks)
            binding.listViewLinks.adapter = linksAdapter

        }
    }

    private fun displayRecentLinks() {
        viewModel.recentLinks.observe(viewLifecycleOwner) { recentlinks ->
            val linksAdapter = LinksAdapter(recentlinks)
            binding.listViewLinks.adapter = linksAdapter
            recentLinksData
            setUpRecentLinkChart()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpDate() {
        binding.dateTextView.setOnClickListener {
            showDatePickerDialog()
        }
        val calendar = Calendar.getInstance()
        selectedStartDate = Calendar.getInstance().apply {
            set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
        }
        selectedEndDate = Calendar.getInstance().apply {
            set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            )
        }
        updateDateRangeText()
        updateChart(selectedStartDate, selectedEndDate)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerDialog() {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    selectedStartDate.apply {
                        set(year, month, 1)
                    }
                    selectedEndDate.apply {
                        set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                    updateDateRangeText()
                    updateChart(selectedStartDate, selectedEndDate)
                },
                selectedStartDate.get(Calendar.YEAR),
                selectedStartDate.get(Calendar.MONTH),
                selectedStartDate.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog?.show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateRangeText() {
        val startDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedStartDate.time)
        val endDateString =
            SimpleDateFormat("dd MMM ", Locale.getDefault()).format(selectedEndDate.time)
        val selectedRange = "$startDateString - $endDateString"
        binding.dateTextView.text = selectedRange
    }


    private fun updateChart(startDate: Calendar, endDate: Calendar) {
        viewModel.chartDataEntries.observe(viewLifecycleOwner) { chartdataentries ->
            val dataSet = LineDataSet(chartdataentries, " ")
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2f
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()

            viewModel.chartLabels.observe(viewLifecycleOwner) { chartlabels ->
                val xAxis = chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = IndexAxisValueFormatter(chartlabels)

                val yAxis = chart.axisLeft
                yAxis.axisMinimum = 0f
                yAxis.axisMaximum = 50f
                yAxis.setLabelCount(5, true)
                yAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return (value.toInt() * 25).toString()
                    }
                }

                //filter dates
                val filteredDataEntries = chartdataentries.filterIndexed { index, _ ->
                    val date = chartlabels.getOrNull(index)?.let { parseChartLabelToDate(it) }
                    date?.let { it.after(startDate) && it.before(endDate) } ?: false
                }

                val filteredLineData = LineData(LineDataSet(filteredDataEntries, " "))
                chart.data = filteredLineData
                chart.notifyDataSetChanged()

                val chartWidth = chart.width.toFloat()

                val xAxisMinValue = 0f
                val xAxisMaxValue = filteredDataEntries.size.toFloat() - 1

                val yAxisMinValue = 0f
                val yAxisMaxValue = 100f

                xAxis.axisMinimum = xAxisMinValue
                xAxis.axisMaximum = xAxisMaxValue

                yAxis.axisMinimum = yAxisMinValue
                yAxis.axisMaximum = yAxisMaxValue

                yAxis.setDrawGridLines(false)
                val visibleRange = chartWidth / filteredDataEntries.size
                xAxis.setLabelCount((chartWidth / visibleRange).toInt(), true)

                chart.description.isEnabled = false
                chart.legend.isEnabled = false
                chart.setDrawMarkers(true)
                chart.fitScreen()

                chart.setTouchEnabled(false)
                chart.isDragEnabled = false
                chart.setScaleEnabled(true)
                chart.setPinchZoom(false)

                chart.isScaleXEnabled = false
                chart.isScaleYEnabled = false
                chart.isDragEnabled = false

                chart.animateY(1000, Easing.EaseInOutCubic)
            }
        }
    }


    private fun parseChartLabelToDate(chartLabel: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val date = sdf.parse(chartLabel)
            Calendar.getInstance().apply {
                time = date
            }
        } catch (e: ParseException) {
            null
        }
    }


    private fun setUpRecentLinkChart() {
        var numDataPoints: Long = 0
        viewModel.chartDataEntries.observe(viewLifecycleOwner) { chartdataentries ->
            val dataSet = LineDataSet(chartdataentries, " ")
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2f
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
            numDataPoints = chartdataentries.size.toLong()
            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }

        viewModel.chartLabels.observe(viewLifecycleOwner) { chartlabels ->
            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setLabelCount(5, true)
            xAxis.valueFormatter = IndexAxisValueFormatter(chartlabels)


            val rightYAxis = chart.axisRight
            rightYAxis.isEnabled = false


            val yAxis = chart.axisLeft
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = recentLinksData!![0].totalClicks.toFloat()
            yAxis.setLabelCount(5, true)
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return (value.toInt() * 25).toString()
                }
            }
            val chartWidth = resources.displayMetrics.widthPixels.toFloat()
            val xAxisMinValue = 0f
            val xAxisMaxValue = numDataPoints - 1f
            val yAxisMinValue = 0f
            val yAxisMaxValue = 100f

            xAxis.axisMinimum = xAxisMinValue
            xAxis.axisMaximum = xAxisMaxValue
            yAxis.axisMinimum = yAxisMinValue
            yAxis.axisMaximum = yAxisMaxValue
            val visibleRange = chartWidth / numDataPoints
            xAxis.setLabelCount((chartWidth / visibleRange).toInt(), true)


            yAxis.setDrawGridLines(false)

            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.setDrawMarkers(false)
            chart.setBackgroundColor(Color.WHITE)
            chart.fitScreen()
            chart.setTouchEnabled(false)
            chart.isDragEnabled = false
            chart.setScaleEnabled(false)
            chart.setPinchZoom(false)
            chart.isScaleXEnabled = false
            chart.isScaleYEnabled = false
            chart.isDragEnabled = false

            chart.animateY(1000, Easing.EaseInOutCubic)
        }
    }

}






