package com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters

import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter
import com.lucidsoftworksllc.taxidi.others.models.server_responses.ProfileNotification

class DriverNotificationsListAdapter (
    callBack: (selectedNotification: ProfileNotification) -> Unit
) : BaseRecyclerViewAdapter<ProfileNotification>(callBack) {

    override fun getLayoutRes(viewType: Int) = R.layout.item_notification
}