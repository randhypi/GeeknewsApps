package com.indev.geeknewsapps.ui.setting

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.indev.geeknewsapps.R
import com.indev.geeknewsapps.ui.category.event.CategoryEventData
import com.indev.geeknewsapps.ui.category.game.CategoryGameData
import com.indev.geeknewsapps.ui.category.movie.CategoryMovieData
import com.itextpdf.io.IOException
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import kotlinx.android.synthetic.main.activity_report.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


@Suppress("DEPRECATION")
class ReportActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    val pdfPath = Environment.getExternalStorageDirectory()


    var pageHeight = 1120
    var pagewidth = 792

    var bmp: Bitmap? = null
    var scaledbmp: Bitmap? = null

    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        try {
            if (checkPermission()) {
                Toast.makeText(this, "Hak akses penyimpanan diizinkan", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
            ib_userReport.setOnClickListener(this)
            ib_games.setOnClickListener(this)
            ib_event.setOnClickListener(this)
            ib_movie.setOnClickListener(this)
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 =
            ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Hak akses penyimpanan diizinkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Hak akses penyimpanan belum diizinkan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_userReport -> {
                try {
                    generatePdfUser()
                    Toast.makeText(this, "Berhasil, silahkan lihat di folder download anda", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "${e}", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.ib_games -> {
                try {
                    generatePdfGame()
                    Toast.makeText(this, "Berhasil, silahkan lihat di folder download anda", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "${e}", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.ib_event -> {
                try {
                    generatePdfEvent()
                    Toast.makeText(this, "Berhasil, silahkan lihat di folder download anda", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "${e}", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.ib_movie -> {
                try {
                    generatePdfMovie()
                    Toast.makeText(this, "Berhasil, silahkan lihat di folder download anda", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "${e}", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun generatePdfEvent() {
        val pathPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pathPdf, setNamePdf("Event"))
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        pdfDocument.addEventHandler(
            PdfDocumentEvent.END_PAGE,
            WatermarkingEventHandler(getImageKop(R.drawable.nokop))
        )


        val dataEvent = CategoryEventData.listData
        document.setMargins(0f, 0f, 0f, 0f)

        val setColorBackroundHeaderTable = DeviceRgb(249, 255, 167)
        val table =
            Table(
                UnitValue.createPercentArray(
                    floatArrayOf(
                        2f,
                        10f,
                        10f,
                        3f,
                        10f
                    )
                )
            ).useAllAvailableWidth()

        table.setMarginRight(10f)
        table.setMarginLeft(10f)
        table.setMarginTop(180f)

        table.addCell(
            Cell().add(
                Paragraph("Kategori").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Title").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Deskripsi").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Waktu").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Penulis/Pengirim").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )


        for (position in 0 until dataEvent.size - 1) {
            val title = CategoryEventData.listData.get(position).title
            val description = CategoryEventData.listData.get(position).description
            val category = CategoryEventData.listData.get(position).category
            val postTime = CategoryEventData.listData.get(position).postTime
            val postBy = CategoryEventData.listData.get(position).postBy



            table.addCell(
                Cell().add(Paragraph(category).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(title).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(
                    Paragraph(
                        description.substring(
                            0,
                            100
                        ) + " ..."
                    ).setTextAlignment(TextAlignment.LEFT)
                ).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postTime).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postBy).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
        }

        val getDate = Paragraph(getDate())
        val getHormat = Paragraph("\nHormat kami,").setTextAlignment(TextAlignment.RIGHT)
        val getTtd  = Paragraph("Riyan Agustiar Sutardi \n\n").setTextAlignment(TextAlignment.RIGHT)
        val getChief = Paragraph( "Chief Project Creative").setBold().setTextAlignment(TextAlignment.RIGHT)

        document.add(table)
        document.add(getDate.setTextAlignment(TextAlignment.RIGHT).setMarginRight(50f))
        document.add(getHormat.setMarginRight(70f))
        document.add(getTtd.setMarginRight(60f))
        document.add(getChief.setMarginRight(60f))
        document.close()
    }

    private fun generatePdfMovie() {
        val pathPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pathPdf, setNamePdf("Movies"))
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        pdfDocument.addEventHandler(
            PdfDocumentEvent.END_PAGE,
            WatermarkingEventHandler(getImageKop(R.drawable.nokop))
        )


        val dataMovie = CategoryMovieData.listData
        document.setMargins(0f, 0f, 0f, 0f)

        val setColorBackroundHeaderTable = DeviceRgb(249, 255, 167)
        val table =
            Table(
                UnitValue.createPercentArray(
                    floatArrayOf(
                        2f,
                        10f,
                        10f,
                        3f,
                        10f
                    )
                )
            ).useAllAvailableWidth()

        table.setMarginRight(10f)
        table.setMarginLeft(10f)
        table.setMarginTop(190f)

        table.addCell(
            Cell().add(
                Paragraph("Kategori").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Title").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Deskripsi").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Waktu").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Penulis/Pengirim").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )


        for (position in 0 until dataMovie.size - 1) {
            val title = CategoryMovieData.listData.get(position).title
            val description = CategoryMovieData.listData.get(position).description
            val category = CategoryMovieData.listData.get(position).category
            val postTime = CategoryMovieData.listData.get(position).postTime
            val postBy = CategoryMovieData.listData.get(position).postBy



            table.addCell(
                Cell().add(Paragraph(category).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(title).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(
                    Paragraph(
                        description.substring(
                            0,
                            100
                        ) + " ..."
                    ).setTextAlignment(TextAlignment.LEFT)
                ).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postTime).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postBy).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
        }

        val getDate = Paragraph(getDate())
        val getHormat = Paragraph("\nHormat kami,").setTextAlignment(TextAlignment.RIGHT)
        val getTtd  = Paragraph("Riyan Agustiar Sutardi \n\n").setTextAlignment(TextAlignment.RIGHT)
        val getChief = Paragraph( "Chief Project Creative").setBold().setTextAlignment(TextAlignment.RIGHT)

        document.add(table)
        document.add(getDate.setTextAlignment(TextAlignment.RIGHT).setMarginRight(50f))
        document.add(getHormat.setMarginRight(70f))
        document.add(getTtd.setMarginRight(60f))
        document.add(getChief.setMarginRight(60f))
        document.close()
    }

    private fun generatePdfUser() {
        mAuth = FirebaseAuth.getInstance()
        val dataUser = mAuth.currentUser
        val dataProviderId = dataUser?.providerId
        val dataEMail = dataUser?.email
        val dataUid = dataUser?.uid

        val pathPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pathPdf, setNamePdf("User"))
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        pdfDocument.addEventHandler(
            PdfDocumentEvent.END_PAGE,
            WatermarkingEventHandler(getImageKop(R.drawable.nokop))
        )

        document.setMargins(0f, 0f, 0f, 0f)

        val setColorBackroundHeaderTable = DeviceRgb(249, 255, 167)


        val table =
            Table(UnitValue.createPercentArray(floatArrayOf(2f, 10f))).useAllAvailableWidth()
        table.setMarginRight(10f)
        table.setMarginLeft(10f)
        table.setMarginTop(200f)

        table.addCell(
            Cell().add(
                Paragraph("Provider").setBold().setTextAlignment(TextAlignment.CENTER)
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(Cell().add(Paragraph(dataProviderId).setTextAlignment(TextAlignment.CENTER)))

        table.addCell(
            Cell().add(
                Paragraph("Email").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(Cell().add(Paragraph(dataEMail).setTextAlignment(TextAlignment.CENTER)))

        table.addCell(
            Cell().add(Paragraph("ID").setTextAlignment(TextAlignment.CENTER).setBold())
                .setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(Cell().add(Paragraph(dataUid).setTextAlignment(TextAlignment.CENTER)))



        val getDate = Paragraph(getDate())
        val getHormat = Paragraph("\nHormat kami,").setTextAlignment(TextAlignment.RIGHT)
        val getTtd  = Paragraph("Riyan Agustiar Sutardi \n\n").setTextAlignment(TextAlignment.RIGHT)
        val getChief = Paragraph( "Chief Project Creative").setBold().setTextAlignment(TextAlignment.RIGHT)

        document.add(table)
        document.add(getDate.setTextAlignment(TextAlignment.RIGHT).setMarginRight(50f))
        document.add(getHormat.setMarginRight(70f))
        document.add(getTtd.setMarginRight(60f))
        document.add(getChief.setMarginRight(60f))
        document.close()

    }

    private fun generatePdfGame() {
        val pathPdf = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        val file = File(pathPdf, setNamePdf("Game"))
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        pdfDocument.addEventHandler(
            PdfDocumentEvent.END_PAGE,
            WatermarkingEventHandler(getImageKop(R.drawable.nokop))
        )


        val dataGame = CategoryGameData.listData
        document.setMargins(0f, 0f, 0f, 0f)

        val setColorBackroundHeaderTable = DeviceRgb(249, 255, 167)
        val table =
            Table(
                UnitValue.createPercentArray(
                    floatArrayOf(
                        2f,
                        10f,
                        10f,
                        3f,
                        10f
                    )
                )
            ).useAllAvailableWidth()

        table.setMarginRight(10f)
        table.setMarginLeft(10f)
        table.setMarginTop(190f)
        table.addCell(
            Cell().add(
                Paragraph("Kategori").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Title").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Deskripsi").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Waktu").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )
        table.addCell(
            Cell().add(
                Paragraph("Penulis/Pengirim").setTextAlignment(TextAlignment.CENTER).setBold()
            ).setBackgroundColor(setColorBackroundHeaderTable).setPadding(2f)
        )


        for (position in 0 until dataGame.size - 1) {
            val title = CategoryGameData.listData.get(position).title
            val description = CategoryGameData.listData.get(position).description
            val category = CategoryGameData.listData.get(position).category
            val postTime = CategoryGameData.listData.get(position).postTime
            val postBy = CategoryGameData.listData.get(position).postBy



            table.addCell(
                Cell().add(Paragraph(category).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(title).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(
                    Paragraph(
                        description.substring(
                            0,
                            100
                        ) + " ..."
                    ).setTextAlignment(TextAlignment.LEFT)
                ).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postTime).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
            table.addCell(
                Cell().add(Paragraph(postBy).setTextAlignment(TextAlignment.LEFT)).setPadding(2f)
            )
        }

        val getDate = Paragraph(getDate())
        val getHormat = Paragraph("\nHormat kami,").setTextAlignment(TextAlignment.RIGHT)
        val getTtd  = Paragraph("Riyan Agustiar Sutardi \n\n").setTextAlignment(TextAlignment.RIGHT)
        val getChief = Paragraph( "Chief Project Creative").setBold().setTextAlignment(TextAlignment.RIGHT)

        document.add(table)
        document.add(getDate.setTextAlignment(TextAlignment.RIGHT).setMarginRight(50f))
        document.add(getHormat.setMarginRight(70f))
        document.add(getTtd.setMarginRight(60f))
        document.add(getChief.setMarginRight(60f))
        document.close()

    }



    fun setNamePdf(name: String): String {
        val day = Date().day
        val date = Date().date
        val month = Date().month
        val year = Date().year.toString()
        val years = year.substring(1,3)

        val hour = Date().hours
        val minute = Date().minutes
        val seconds = Date().seconds

        val dayName = when (day) {
            1 -> "Senin"
            2 -> "Selasa"
            3 -> "Rabu"
            4 -> "Kamis"
            5 -> "Jum'at"
            6 -> "Sabtu"
            7 -> "Minggu"

            else -> "No Day"
        }
        val monthName = when (month) {
            0 -> "Januari"
            1 -> "Februari"
            2 -> "Maret"
            3 -> "April"
            4 -> "Mei"
            5 -> "Juni"
            6 -> "Juli"
            7 -> "Agustus"
            8 -> "September"
            9 -> "Oktober"
            10 -> "November"
            11 -> "Desember"

            else -> "No Month"
        }


        return "$name $dayName $date $monthName $years $hour $minute $seconds.pdf"
    }

    fun getDate(): String {
        val place = getString(R.string.jakarta)
        val day = Date().day
        val date = Date().date
        val month = Date().month
        val year = Date().year.toString()
        val years = year.substring(1,3)

        val dayName = when (day) {
            1 -> "Senin"
            2 -> "Selasa"
            3 -> "Rabu"
            4 -> "Kamis"
            5 -> "Jum'at"
            6 -> "Sabtu"
            7 -> "Minggu"

            else -> "No Day"
        }
        val monthName = when (month) {
            0 -> "Januari"
            1 -> "Februari"
            2 -> "Maret"
            3 -> "April"
            4 -> "Mei"
            5 -> "Juni"
            6 -> "Juli"
            7 -> "Agustus"
            8 -> "September"
            9 -> "Oktober"
            10 -> "November"
            11 -> "Desember"

            else -> "No Month"
        }

        return "$place, $dayName $date $monthName 20$years "
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getImageKop(image: Int): Image {
        val kopImage: Bitmap = BitmapFactory.decodeResource(resources, image)

        val stream = ByteArrayOutputStream()
        kopImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteMapDataKop = stream.toByteArray()
        val imageData: ImageData = ImageDataFactory.create(byteMapDataKop)
        return Image(imageData)
    }

    private class WatermarkingEventHandler(image: Image) : IEventHandler, AppCompatActivity() {

        val imageKop = image


        override fun handleEvent(currentEvent: Event) {
            val docEvent = currentEvent as PdfDocumentEvent
            val pdfDoc = docEvent.document
            val page = docEvent.page
            var font: PdfFont? = null
            try {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            } catch (e: IOException) {

                // Such an exception isn't expected to occur,
                // because helvetica is one of standard fonts
                System.err.println(e.message)
            }
            val canvas = PdfCanvas(page.newContentStreamBefore(), page.resources, pdfDoc)

            com.itextpdf.layout.Canvas(canvas, page.pageSize)
                .add(imageKop)
                .close()


        }
    }

}