
import java.text.SimpleDateFormat
import java.util.*

fun timeFormatter(hour: Int, minute: Int): String{
    val calendar = Calendar.getInstance()
    calendar.set(0, 0, 0, hour, minute)

    return SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(calendar.time)
}