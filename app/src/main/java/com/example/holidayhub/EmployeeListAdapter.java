package com.example.holidayhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder  > {
    Context context;
    ArrayList <EmployeeList> employeeListArrayList;

    public EmployeeListAdapter(Context context, ArrayList<EmployeeList> employeeListArrayList) {
        this.context = context;
        this.employeeListArrayList = employeeListArrayList;
    }

    @NonNull
    @Override
    public EmployeeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.employee_lists,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeListAdapter.MyViewHolder holder, int position) {
        EmployeeList employeeList =employeeListArrayList.get(position);

        holder.firstName.setText("First Name: "+employeeList.firstName);
        holder.lastName.setText("Last Name: "+employeeList.lastName);
        holder.mobile.setText("Mobile: "+employeeList.mobile);
        holder.email.setText("Email: "+ employeeList.email);
        holder.role.setText("Role: "+employeeList.role);


    }

    @Override
    public int getItemCount() {
        return employeeListArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView firstName, lastName, mobile, email, role;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName =itemView.findViewById(R.id.firstNameTextViewEmployee);
            lastName=itemView.findViewById(R.id.lastNameTextViewEmployee);
            mobile=itemView.findViewById(R.id.mobileTextViewEmployee);
            email=itemView.findViewById(R.id.emailTextViewEmployee);
            role =itemView.findViewById(R.id.roleTextViewEmployee);

        }
    }
}
