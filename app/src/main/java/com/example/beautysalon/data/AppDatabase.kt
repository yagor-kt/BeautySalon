package com.example.beautysalon.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.beautysalon.data.dao.MasterDao
import com.example.beautysalon.data.dao.ServiceDao
import com.example.beautysalon.data.entity.Appointment
import com.example.beautysalon.data.entity.Master
import com.example.beautysalon.data.entity.Service
import java.util.Calendar

@Database(
    entities = [Master::class, Service::class, Appointment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun masterDao(): MasterDao
    abstract fun serviceDao(): ServiceDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "beauty_salon.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                    .also { instance = it }
            }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val masters = listOf(
                arrayOf<Any>("Анна", "Маникюр", 6, 4.9, "https://i.pravatar.cc/300?img=5", "Специалист по маникюру и уходу за ногтями.", 8),
                arrayOf<Any>("Мария", "Парикмахер", 8, 5.0, "https://i.pravatar.cc/300?img=12", "Мастер стрижек, окрашивания и укладок.", 8),
                arrayOf<Any>("Ольга", "Косметолог", 5, 4.8, "https://i.pravatar.cc/300?img=32", "Специалист по уходу за кожей лица.", 8),
                arrayOf<Any>("Ирина", "Массаж", 10, 4.7, "https://i.pravatar.cc/300?img=47", "Мастер расслабляющего и оздоровительного массажа.", 8)
            )

            masters.forEach { values ->
                db.execSQL(
                    """
                    INSERT INTO masters
                    (name, specialization, experienceYears, rating, photoUrl, description, dailySlots)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    values
                )
            }

            val services = listOf(
                arrayOf<Any>("Маникюр классический", "Маникюр", 60, 1500),
                arrayOf<Any>("Маникюр с покрытием", "Маникюр", 90, 2500),
                arrayOf<Any>("Стрижка женская", "Парикмахерские", 60, 1800),
                arrayOf<Any>("Окрашивание волос", "Парикмахерские", 120, 3500),
                arrayOf<Any>("Уход за лицом", "Косметология", 60, 3000),
                arrayOf<Any>("Чистка лица", "Косметология", 90, 3200),
                arrayOf<Any>("Массаж спины", "Массаж", 45, 1200),
                arrayOf<Any>("Расслабляющий массаж", "Массаж", 90, 2800)
            )

            services.forEach { values ->
                db.execSQL(
                    """
                    INSERT INTO services (title, category, durationMin, price)
                    VALUES (?, ?, ?, ?)
                    """.trimIndent(),
                    values
                )
            }

            fun timestamp(dayOffset: Int, hour: Int, minute: Int): Long {
                return Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    add(Calendar.DAY_OF_YEAR, dayOffset)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis
            }

            fun addAppointment(
                client: String,
                master: String,
                service: String,
                price: Int,
                dayOffset: Int,
                hour: Int,
                minute: Int,
                status: String
            ) {
                db.execSQL(
                    """
                    INSERT INTO appointments
                    (clientName, masterName, serviceTitle, price, dateTime, status)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf(
                        client,
                        master,
                        service,
                        price,
                        timestamp(dayOffset, hour, minute),
                        status
                    )
                )
            }

            // Пять записей на сегодня.
            addAppointment("Иванова", "Анна", "Маникюр с покрытием", 2500, 0, 10, 0, "PENDING")
            addAppointment("Петрова", "Мария", "Стрижка женская", 1800, 0, 11, 30, "PENDING")
            addAppointment("Сидорова", "Ольга", "Уход за лицом", 3000, 0, 13, 0, "PENDING")
            addAppointment("Кузнецова", "Ирина", "Массаж спины", 1200, 0, 15, 0, "PENDING")
            addAppointment("Смирнова", "Анна", "Маникюр классический", 1500, 0, 17, 0, "PENDING")

            // Пять выполненных записей за последние дни.
            addAppointment("Попова", "Мария", "Окрашивание волос", 3500, -1, 10, 0, "DONE")
            addAppointment("Васильева", "Анна", "Маникюр с покрытием", 2500, -2, 12, 0, "DONE")
            addAppointment("Михайлова", "Ирина", "Расслабляющий массаж", 2800, -3, 14, 0, "DONE")
            addAppointment("Новикова", "Ольга", "Чистка лица", 3200, -5, 11, 0, "DONE")
            addAppointment("Фёдорова", "Мария", "Стрижка женская", 1800, -6, 16, 0, "DONE")

            // Пять выполненных записей за прошлый месяц.
            val previousMonthOffset = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.DAY_OF_MONTH, -1)
            }.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

            addAppointment("Алексеева", "Анна", "Маникюр классический", 1500, previousMonthOffset, 10, 0, "DONE")
            addAppointment("Лебедева", "Мария", "Окрашивание волос", 3500, previousMonthOffset - 3, 12, 30, "DONE")
            addAppointment("Семёнова", "Ольга", "Уход за лицом", 3000, previousMonthOffset - 7, 13, 0, "DONE")
            addAppointment("Егорова", "Ирина", "Массаж спины", 1200, previousMonthOffset - 10, 15, 0, "DONE")
            addAppointment("Павлова", "Анна", "Маникюр с покрытием", 2500, previousMonthOffset - 14, 16, 0, "DONE")

            // Три отменённые записи.
            addAppointment("Козлова", "Мария", "Стрижка женская", 1800, -1, 9, 0, "CANCELLED")
            addAppointment("Степанова", "Ольга", "Чистка лица", 3200, -4, 14, 30, "CANCELLED")
            addAppointment("Николаева", "Ирина", "Расслабляющий массаж", 2800, previousMonthOffset - 2, 17, 0, "CANCELLED")
        }
    }
}