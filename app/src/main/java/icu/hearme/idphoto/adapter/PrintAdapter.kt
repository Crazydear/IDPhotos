package icu.hearme.idphoto.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import icu.hearme.idphoto.R
import java.io.FileOutputStream


object BitmapPrinter {

    fun printBitmap(context: Context, bitmap: Bitmap, jobName: String) {
        // 获取打印服务
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.NA_INDEX_4X6)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()

        // 创建打印适配器
        val printAdapter = object : PrintDocumentAdapter() {
            override fun onWrite(pages: Array<PageRange>, destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal, callback: WriteResultCallback
            ) {
                try {
                    // 将Bitmap写入PDF文档
                    val document = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                    val page = document.startPage(pageInfo)

                    // 将Bitmap绘制到PDF页面
                    val canvas = page.canvas
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    document.finishPage(page)

                    // 写入输出流
                    FileOutputStream(destination.fileDescriptor).use { outputStream ->
                        document.writeTo(outputStream)
                    }

                    document.close()

                    // 通知打印成功
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    // 打印失败回调
                    callback.onWriteFailed(e.message)
                }
            }

            override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal, callback: LayoutResultCallback, extras: Bundle?
            ) {
                // 检查是否取消
                if (cancellationSignal.isCanceled) {
                    callback.onLayoutCancelled()
                    return
                }

                // 创建打印文档信息
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_PHOTO)
                    .setPageCount(1)
                    .build()

                // 返回布局结果
                callback.onLayoutFinished(info, true)
            }
        }

        // 开始打印作业
        printManager.print(jobName, printAdapter, printAttributes)
    }

    fun printDocument(context: Context, bitmaps: List<Bitmap>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${context.getString(R.string.app_name)} 打印任务"
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
            .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
            .build()
        // 创建打印适配器
        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                // 创建文档信息
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(bitmaps.size)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    destination?.use { parcelFileDescriptor ->
                        val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)

                        // 创建PDF文档
                        val pdfDocument = PdfDocument()

                        // 添加每一页
                        bitmaps?.forEachIndexed { index, pageBitmap ->
                            val pageInfo = PdfDocument.PageInfo.Builder(
                                pageBitmap.width,
                                pageBitmap.height,
                                index
                            ).create()

                            val page = pdfDocument.startPage(pageInfo)
                            val canvas = page.canvas

                            // 绘制图片
                            canvas.drawBitmap(pageBitmap, 0f, 0f, null)

                            pdfDocument.finishPage(page)
                        }

                        // 写入输出流
                        pdfDocument.writeTo(fileOutputStream)
                        pdfDocument.close()
                        fileOutputStream.close()
                    }

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }
        printManager.print(jobName, printAdapter, printAttributes)
    }
}