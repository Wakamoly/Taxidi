package com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters

import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter
import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverHomeViewResponseModel

class DriverHomeNewsListAdapter (
        callBack: (selectedItem: DriverHomeViewResponseModel.NewsResult) -> Unit
) : BaseRecyclerViewAdapter<DriverHomeViewResponseModel.NewsResult>(callBack) {

    override fun getLayoutRes(viewType: Int) = R.layout.item_driver_home_news
}