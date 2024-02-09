package com.example.holidayhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class userRequestsAdapter extends RecyclerView.Adapter<userRequestsAdapter.UserRequestViewHolder> {

    Context context;
    ArrayList<userRequestsView> userRequestsViewArrayList;

    public userRequestsAdapter(Context context, ArrayList<userRequestsView> userRequestsViewArrayList) {
        this.context = context;
        this.userRequestsViewArrayList = userRequestsViewArrayList;
    }

    @NonNull
    @Override
    public UserRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userholidaysrequest_view, parent, false);
        return new UserRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRequestViewHolder holder, int position) {
        userRequestsView userRequestsView = userRequestsViewArrayList.get(position);

        if (userRequestsView != null) {
            // Use getParsedStartDate and getParsedEndDate to obtain Date objects
            Date startDate = userRequestsView.getParsedStartDate();
            Date endDate = userRequestsView.getParsedEndDate();

            // Format Date objects to strings
            String startDateStr = formatDate(startDate);
            String endDateStr = formatDate(endDate);
            String submittedAtStr = formatDate(userRequestsView.submittedAt);

            // Set the formatted dates to TextViews
            holder.txtRequestStartDate.setText("Start Date: "+startDateStr);
            holder.txtRequestEndDate.setText("End Date: "+endDateStr);
            holder.txtRequestReason.setText("Reason For Holiday: "+userRequestsView.reason);
            holder.txtRequestStatus.setText("Status: "+userRequestsView.status);
            holder.txtRequestHolidayType.setText("Type: "+userRequestsView.type);
            holder.txtSubmittedAt.setText("Submitted At: "+submittedAtStr);
        }
    }

    @Override
    public int getItemCount() {
        return userRequestsViewArrayList.size();
    }

    public static class UserRequestViewHolder extends RecyclerView.ViewHolder {

        TextView txtRequestStartDate, txtRequestEndDate, txtRequestStatus, txtRequestHolidayType, txtRequestReason, txtSubmittedAt;

        public UserRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRequestStartDate = itemView.findViewById(R.id.txtRequestStartDate);
            txtRequestEndDate = itemView.findViewById(R.id.txtRequestEndDate);
            txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
            txtRequestHolidayType = itemView.findViewById(R.id.txtRequestHolidayType);
            txtRequestReason = itemView.findViewById(R.id.txtRequestReason);
            txtSubmittedAt = itemView.findViewById(R.id.txtSubmittedAt);
        }
    }

    private String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return dateFormat.format(date);
        }
        return "";
    }
}
