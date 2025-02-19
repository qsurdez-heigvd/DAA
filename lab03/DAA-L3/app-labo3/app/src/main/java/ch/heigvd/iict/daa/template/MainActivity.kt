// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.daa.template

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.util.Calendar
import java.util.Date

/**
 * MainActivity class that handles the main user interface and interactions.
 */
class MainActivity : AppCompatActivity() {
    private val tag: String = javaClass.simpleName

    // UI elements
    private lateinit var dateEdit: EditText
    private lateinit var dateToggle: ToggleButton
    private lateinit var occupationStudentButton: RadioButton
    private lateinit var occupationEmployeeButton: RadioButton
    private lateinit var groupStudent: Group
    private lateinit var groupWorker: Group
    private lateinit var cancelBtn: Button
    private lateinit var okBtn: Button
    private var birthDate: Long = Calendar.getInstance().timeInMillis // First value is today
    private lateinit var sectorSpinner: Spinner
    private lateinit var nationalitySpinner: Spinner

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize behaviors
        actionsButtonsBehaviors()
        dateBehavior()
        radioButtonBehavior()
        spinnerBehavior()
    }

    /**
     * Inflates the menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Handle the menu item selection.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_load_worker -> {
                fillExampleWorker()
                true
            }
            R.id.menu_load_student -> {
                fillExampleStudent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Add behaviors to the OK, Cancel buttons at bottom, in addition to prefill Student and Worker
     * buttons
     */
    private fun actionsButtonsBehaviors() {
        // Cancel button - it will just empty all available fields in the form
        cancelBtn = findViewById(R.id.buttonCancel)
        val editFields: IntArray =
            intArrayOf(
                R.id.editName,
                R.id.editFirstName,
                R.id.editStudentUniversity,
                R.id.editStudentGraduationYear,
                R.id.editWorkerCompany,
                R.id.editAdditionalEmail,
                R.id.editWorkerExperience,
                R.id.editAdditionalRemark,
            )
        cancelBtn.setOnClickListener {
            // Reset all EditText (including number inputs)
            for (f in editFields) {
                findViewById<EditText>(f).text.clear()
            }
            // Reset all spinners
            findViewById<Spinner>(R.id.sectorSpinner).setSelection(0)
            findViewById<Spinner>(R.id.nationalitySpinner).setSelection(0)
            // Reset birth dates
            birthDate = Calendar.getInstance().timeInMillis
            refreshBirthDatePreview()
            // Reset radio group
            findViewById<RadioGroup>(R.id.radioGroupOccupation).clearCheck()
            // And hide back the 2 groups
            groupWorker.visibility = View.GONE
            groupStudent.visibility = View.GONE
        }

        okBtn = findViewById(R.id.buttonOk)
        okBtn.setOnClickListener {
            val radioId = findViewById<RadioGroup>(R.id.radioGroupOccupation).checkedRadioButtonId
            if (radioId == -1) {
                Log.i(tag, R.string.submit_error_notification.toString())
                Toast.makeText(this, R.string.submit_error_notification, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val name = readTextInput(R.id.editName)
                val firstname = readTextInput(R.id.editFirstName)
                val finalDate = Calendar.getInstance()
                finalDate.time = Date.from(Instant.ofEpochMilli(birthDate))
                val nationality =
                    findViewById<Spinner>(R.id.nationalitySpinner).selectedItem.toString()
                val email = readTextInput(R.id.editAdditionalEmail)
                val remark = readTextInput(R.id.editAdditionalRemark)
                if (radioId == R.id.buttonStudent) {
                    val university = readTextInput(R.id.editStudentUniversity)
                    val graduationYear = extractNumberFromInput(R.id.editStudentGraduationYear)
                    val student = Student(
                        name,
                        firstname,
                        finalDate,
                        nationality,
                        university,
                        graduationYear,
                        email,
                        remark
                    )
                    Log.i(tag, student.toString())
                } else {
                    val sector =
                        findViewById<Spinner>(R.id.sectorSpinner).selectedItem.toString()
                    val company = readTextInput(R.id.editWorkerCompany)
                    val xp = extractNumberFromInput(R.id.editWorkerExperience)
                    Log.i(
                        tag,
                        Worker(
                            name,
                            firstname,
                            finalDate,
                            nationality,
                            company,
                            sector,
                            xp,
                            email,
                            remark
                        ).toString()
                    )
                }
            }
        }
    }

    /**
     * Sets up the behavior for the radio buttons to toggle visibility of student and worker groups.
     */
    private fun radioButtonBehavior() {
        occupationStudentButton = findViewById(R.id.buttonStudent)
        occupationEmployeeButton = findViewById(R.id.buttonWorker)
        groupStudent = findViewById(R.id.groupStudent)
        groupWorker = findViewById(R.id.groupWorker)

        // Set click listener for student button
        occupationStudentButton.setOnClickListener {
            groupStudent.visibility = View.VISIBLE
            groupWorker.visibility = View.GONE
            updateConstraints()
        }

        // Set click listener for employee button
        occupationEmployeeButton.setOnClickListener {
            groupStudent.visibility = View.GONE
            groupWorker.visibility = View.VISIBLE
            updateConstraints()
        }
    }

    /**
     * Sets up the behavior for the spinners.
     */
    private fun spinnerBehavior() {
       sectorSpinner = findViewById(R.id.sectorSpinner)
        nationalitySpinner = findViewById(R.id.nationalitySpinner)

        val sectors = resources.getStringArray(R.array.sectors).toList()
        val nationalities = resources.getStringArray(R.array.nationalities).toList()

        val sectorAdapter = SpinnerAdapter(this, sectors)
        val nationalityAdapter = SpinnerAdapter(this, nationalities)

        // Apply the adapters to the spinners
        sectorSpinner.adapter = sectorAdapter
        nationalitySpinner.adapter = nationalityAdapter

        // Set the default selection to the first item (the hint)
        sectorSpinner.setSelection(0, false)
        nationalitySpinner.setSelection(0, false)
    }

    /**
     * Updates the constraints of the layout based on the visibility of the student group.
     */
    private fun updateConstraints() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (groupStudent.visibility == View.VISIBLE) {
            constraintSet.connect(
                R.id.textAdditionalTitle,
                ConstraintSet.TOP,
                R.id.groupStudent,
                ConstraintSet.BOTTOM,
                0
            )
        } else {
            constraintSet.connect(
                R.id.textAdditionalTitle,
                ConstraintSet.TOP,
                R.id.groupWorker,
                ConstraintSet.BOTTOM,
                0
            )
        }

        constraintSet.applyTo(constraintLayout)
    }

    /**
     * Sets up the behavior for the date picker and toggle button.
     */
    private fun dateBehavior() {
        dateEdit = findViewById(R.id.editBirthdate)
        dateToggle = findViewById(R.id.toggleButtonBirthdate)

        val today = Calendar.getInstance().timeInMillis
        refreshBirthDatePreview()

        dateToggle.setOnClickListener {
            // Build a date on 01.01.1920 as the oldest valid date
            val calendar = Calendar.getInstance()
            calendar.set(1920, Calendar.JANUARY, 1)
            val startDate = calendar.timeInMillis

            // Create date picker dialog with current birthdate selected, title, min and max date
            val datePickerDialog = MaterialDatePicker
                .Builder
                .datePicker()
                .setTitleText(getString(R.string.main_base_birthdate_dialog_title))
                .setSelection(birthDate) // set selection to previous choice
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setStart(startDate)
                        // The maximum date doesn't fully work, the addOnPositiveButtonClickListener is still called on invalid selection.
                        .setEnd(today)
                        .build()
                )
                .build()

            // Update the date display on date change
            datePickerDialog.addOnPositiveButtonClickListener { selection ->
                var finalTime = selection
                // Make sure the given date is not above now
                val now = Calendar.getInstance().timeInMillis
                if (selection > now) {
                    finalTime = now
                }
                birthDate = finalTime
                refreshBirthDatePreview()
            }

            datePickerDialog.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    /**
     * Fill the form with a person's data.
     */
    private fun fillExamplePerson(person: Person) {
        fillTextInput(R.id.editName, person.name)
        fillTextInput(R.id.editFirstName, person.firstName)
        fillTextInput(R.id.editAdditionalEmail, person.email)
        fillTextInput(R.id.editAdditionalRemark, person.remark)

        birthDate = person.birthDay.timeInMillis
        refreshBirthDatePreview()

        val nationality = (nationalitySpinner.adapter as SpinnerAdapter).getPosition(person.nationality)
        nationalitySpinner.setSelection(nationality)
    }

    /**
     * Fill the form with the example student data.
     */
    private fun fillExampleStudent() {
        occupationStudentButton.performClick()
        val student = Person.exampleStudent
        fillExamplePerson(student)
        fillTextInput(R.id.editStudentUniversity, student.university)
        fillTextInput(R.id.editStudentGraduationYear, student.graduationYear.toString())
        findViewById<RadioGroup>(R.id.radioGroupOccupation).check(R.id.buttonStudent)

        Toast.makeText(this, R.string.student_loaded_notification, Toast.LENGTH_SHORT).show()
    }

    /**
     * Fill the form with the example worker data.
     */
    private fun fillExampleWorker() {
        occupationEmployeeButton.performClick()
        val worker = Person.exampleWorker
        fillExamplePerson(worker)
        fillTextInput(R.id.editWorkerCompany, worker.company)
        fillTextInput(R.id.editWorkerExperience, worker.experienceYear.toString())
        findViewById<RadioGroup>(R.id.radioGroupOccupation).check(R.id.buttonWorker)

        val position = (sectorSpinner.adapter as SpinnerAdapter).getPosition(worker.sector)
        sectorSpinner.setSelection(position)

        Toast.makeText(this, R.string.employee_loaded_notification, Toast.LENGTH_SHORT).show()
    }

    /**
     * Refresh the birthdate preview inside the unmodifiable EditText, based on birthDate attribute
     */
    private fun refreshBirthDatePreview() {
        dateEdit.setText(
            DateUtils.formatDateTime(
                this, birthDate,
                // Always show the date with the year even if it is the current year
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )
    }

    /**
     * Extract the integer inside an EditText of input type number
     */
    private fun extractNumberFromInput(id: Int): Int {
        val txt = findViewById<EditText>(id).text.toString()
        return if (txt.isEmpty()) 0 else Integer.parseInt(txt)
    }

    /**
     * Extract the string value in an EditText
     */
    private fun readTextInput(id: Int): String {
        return findViewById<EditText>(id).text.toString()
    }

    /**
     * Fill the given text input with given content
     */
    private fun fillTextInput(id: Int, content: String) {
        return findViewById<EditText>(id).setText(content)
    }
}
