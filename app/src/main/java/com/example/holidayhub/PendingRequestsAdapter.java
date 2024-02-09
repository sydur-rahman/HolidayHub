package com.example.holidayhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.PendingRequestsViewHolder> {

     Context context;
     ArrayList<PendingRequests> pendingRequestsArrayList;


    public PendingRequestsAdapter(Context context, ArrayList<PendingRequests> pendingRequestsArrayList) {
        this.context = context;
        this.pendingRequestsArrayList = pendingRequestsArrayList;

    }

    @NonNull
    @Override
    public PendingRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_holiday_list, parent, false);
        return new PendingRequestsViewHolder(view);
    }
    public String getHolidayRequestsID(int position) {
        return pendingRequestsArrayList.get(position).getHolidayRequestsId();
    }
    public String getUserID(int position) {
        return pendingRequestsArrayList.get(position).getUserId();
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestsViewHolder holder, int position) {
        PendingRequests pendingRequest = pendingRequestsArrayList.get(position);


        holder.txtRequestFirstName.setText("First Name: "+pendingRequest.firstName);
        holder.txtRequestRole.setText("Role: "+pendingRequest.role);

        holder.txtRequestStartDate.setText("Start: "+pendingRequest.startDate);
        holder.txtRequestEndDate.setText("End: "+pendingRequest.endDate);

        holder.txtRequestType.setText("Type: " + pendingRequest.type);
        holder.txtPendingReason.setText("Reason: " + pendingRequest.reason);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String submittedAtStr = dateFormat.format(pendingRequest.getSubmittedAt());
        holder.txtRequestSubmittedAt.setText("Request Submitted At: " + submittedAtStr);

    }

    @Override
    public int getItemCount() {
        return pendingRequestsArrayList.size();
    }


    public static class PendingRequestsViewHolder extends RecyclerView.ViewHolder {

        //ImageView imgEmployeePhoto;
        TextView txtRequestFirstName, txtRequestRole, txtRequestStartDate, txtRequestEndDate,
                txtRequestType, txtPendingReason, txtRequestSubmittedAt, txtRequestNumberOfDays;
        ImageButton btnAcceptButton, btnRejectButton;

        public PendingRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            //imgEmployeePhoto = itemView.findViewById(R.id.imgEmployeePhoto);
            txtRequestFirstName = itemView.findViewById(R.id.txtRequestFirstName);
            txtRequestRole = itemView.findViewById(R.id.txtRequestRole);
            txtRequestStartDate = itemView.findViewById(R.id.txtRequestStartDate);
            txtRequestEndDate = itemView.findViewById(R.id.txtRequestEndDate);
            txtRequestType = itemView.findViewById(R.id.txtRequestType);
            txtPendingReason = itemView.findViewById(R.id.txtPendingReason);
            txtRequestSubmittedAt = itemView.findViewById(R.id.txtRequestSubmittedAt);
           }
    }

    // Interface to handle click events on Accept and Reject buttons
    /*public interface PendingRequestsClickListener {
        void onAcceptClick(int position, String documentPath);
        void onRejectClick(int position, String documentPath);
    }
*/
}
