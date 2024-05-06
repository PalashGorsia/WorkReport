/*
package com.sdmi.japanadvance.ui.report.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView


class ReportsListAdapter(
    private val fragment: FragmentManager,
    private val context: AppCompatActivity,
  //  private var mArrayList: MutableList<ReportesDataItems>?,
    private val type: Int
) : RecyclerView.Adapter<ReportsListAdapter.ViewHolder>() {


    private var childFragmentManager: FragmentManager? = null
    private var layoutInflater: LayoutInflater? = null

    init {

        // prefConnector = PrefConnector(context)

    }

    */
/**
     * Override method
     *//*

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }

        val binding = DataBindingUtil.inflate<ItemReportsBinding>(
            layoutInflater!!,
            R.layout.item_reports,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun setcallback(callback: AdapterInterface<*, *>) {
        //  this.callback = callback
    }

    */
/**
     * Override method
     *//*

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        mArrayList?.get(position)?.let { item ->


            viewHolder.binding.titleTv.text = item.storeId.storename
            viewHolder.binding.statusTV.text = item.getDate(type)
            xtnLog("type $type")

        //    viewHolder.binding.statusTV.setTextColor(Color.parseColor(item.getStatusTextColor(type)))
          //  viewHolder.binding.statusTV.text = context.getString(R.string.score) + " "

                 //   calculateScoreFromTabs(getListFromString(item.answerData!!)!!.tabs)






*/
/*
            if (type == ReportsEnum.IN_PROGRESS.getValue()) {
                viewHolder.binding.statusTV.visibility = View.INVISIBLE
                viewHolder.binding.btSend.visibility = View.VISIBLE

            } else {
                viewHolder.binding.statusTV.visibility = View.VISIBLE
                viewHolder.binding.btSend.visibility = View.GONE

                viewHolder.binding.llContainer.setOnClickListener {

                    openInspectionDetails(item)
                }
            }
*//*


            viewHolder.binding.llContainer.setOnClickListener {

                openInspectionDetails(item)
            }

            // viewHolder.binding.statusTV.text = item.getStatusName()

*/
/*
            viewHolder.binding.btSend.setOnClickListener {
                openInspectionDetails(item)

            }
*//*


        }
    }

    private fun openInspectionDetails(item: ReportesDataItems) {
        STOREID = item.storeId?._id?:""
        val bundle = Bundle()
        bundle.putString(DATA, item._id)
        bundle.putInt(TYPE, type)
        bundle.putString(DATE,item.getDate(type))
        context.navigateClass<InspectionDetailsActivity>(bundle)
    }


    */
/**
     * Override method
     *//*

    override fun getItemCount(): Int {
        return mArrayList!!.size
    }

    */
/**
     * Override method
     *//*

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun setfragmentManager(childFragmentManager: FragmentManager?) {
        this.childFragmentManager = childFragmentManager
    }

    inner class ViewHolder(internal var binding: ItemReportsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
*/
