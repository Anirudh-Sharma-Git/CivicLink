package com.book.civiclink2o;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.IssueViewHolder> {

    private List<Issue> issueList;
    private Context context;

    public IssuesAdapter(Context context, List<Issue> issueList) {
        this.context = context;
        this.issueList = issueList;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_card_item, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issueList.get(position);

        holder.category.setText(issue.getCategory());
        holder.title.setText(issue.getDescription());
        holder.location.setText(issue.getReportedByName()); // Placeholder, we'll fix this later
        holder.time.setText(issue.getCreatedAt()); // Placeholder, we'll format this later
        holder.upvotes.setText(String.valueOf(issue.getUpvotes()));
        holder.status.setText(issue.getStatus());

        switch (issue.getStatus()) {
            case "Pending":
                holder.status.setBackgroundResource(R.drawable.bg_status_pending);
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
                break;
            case "In Progress":
                holder.status.setBackgroundResource(R.drawable.bg_status_inprogress);
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.status_inprogress_text));
                break;
            case "Resolved":
                holder.status.setBackgroundResource(R.drawable.bg_status_resolved);
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.status_resolved_text));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    // --- THIS IS THE FIX: The missing method ---
    /**
     * Updates the list of issues displayed by the adapter.
     * @param newIssues The new list of issues to display.
     */
    public void updateIssues(List<Issue> newIssues) {
        issueList.clear();
        issueList.addAll(newIssues);
        notifyDataSetChanged(); // This is crucial! It tells the RecyclerView to refresh itself.
    }
    // --- END OF FIX ---


    public static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView category, status, title, location, time, upvotes;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.issue_category);
            status = itemView.findViewById(R.id.issue_status);
            title = itemView.findViewById(R.id.issue_title);
            location = itemView.findViewById(R.id.issue_location);
            time = itemView.findViewById(R.id.issue_time);
            upvotes = itemView.findViewById(R.id.issue_upvotes);
        }
    }
}