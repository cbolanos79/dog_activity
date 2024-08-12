import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dosbotones.dogactivity.R

data class ActivityLog(val date: String, val activity: String, val data: String)

class ActivityLogAdapter(private val activityLogs: List<ActivityLog>) : RecyclerView.Adapter<ActivityLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val dateTextView: TextView = view.findViewById(R.id.text_date)
        val activityTextView: TextView = view.findViewById(R.id.text_activity)
        val dataTextView: TextView = view.findViewById(R.id.text_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activityLog = activityLogs[position]
        holder.dateTextView.text = activityLog.date
        holder.activityTextView.text = activityLog.activity
        holder.dataTextView.text = activityLog.data
    }

    override fun getItemCount(): Int {
        return activityLogs.size
    }
}
