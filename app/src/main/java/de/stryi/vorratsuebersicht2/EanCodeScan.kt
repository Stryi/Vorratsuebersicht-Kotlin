package de.stryi.vorratsuebersicht2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.BarcodeCallback

class EanCodeScan : DialogFragment() {

    var onResult: ((String) -> Unit)? = null
    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ean_code_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = view.findViewById(R.id.zxing_barcode_scanner)

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                barcodeView.pause() // Nur 1x auslösen
                onResult?.invoke(result.text)
                dismiss()
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })

        /*
        val button = view.findViewById<Button>(R.id.EanScanButton)
        button.setOnClickListener {
            onResult?.invoke("4316268474382")
            dismiss()
        }
        */
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}