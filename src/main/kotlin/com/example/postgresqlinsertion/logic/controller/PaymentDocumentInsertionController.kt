package com.example.postgresqlinsertion.logic.controller

import com.example.postgresqlinsertion.logic.dto.ResponseDto
import com.example.postgresqlinsertion.logic.service.PaymentDocumentService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/test-insertion")
class PaymentDocumentInsertionController(
    val service: PaymentDocumentService
) {

    @PostMapping("/copy/{count}")
    fun insertViaCopy(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopy(count)
        }
        return ResponseDto(
            name = "Copy method",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-with-transaction/{count}")
    fun insertViaCopyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyWithTransaction(count)
        }
        return ResponseDto(
            name = "Copy method with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-binary-with-transaction/{count}")
    fun insertViaCopyByBinaryWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyBinaryWithTransaction(count)
        }
        return ResponseDto(
            name = "Copy method by binary with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-property-with-transaction/{count}")
    fun insertViaCopyAndPropertyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyAndKPropertyWithTransaction(count)
        }
        return ResponseDto(
            name = "Copy method by property with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-binary-and-property-with-transaction/{count}")
    fun insertViaCopyByBinaryAndPropertyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyBinaryAndKPropertyWithTransaction(count)
        }
        return ResponseDto(
            name = "Copy method by binary and property with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-file/{count}")
    fun insertViaCopyByFile(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyViaFile(count)
        }
        return ResponseDto(
            name = "Copy method via file",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-binary-file/{count}")
    fun insertViaCopyByBinaryFile(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyViaBinaryFile(count)
        }
        return ResponseDto(
            name = "Copy method via binary file",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/copy-by-file-and-property/{count}")
    fun insertViaCopyByFileAndProperty(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyAnpPropertyViaFile(count)
        }
        return ResponseDto(
            name = "Copy method via file and property",
            count = count,
            time = getTimeString(time)
        )
    }


    @PostMapping("/copy-by-binary-file-and-property/{count}")
    fun insertViaCopyByBinaryFileAndProperty(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyAnpPropertyViaBinaryFile(count)
        }
        return ResponseDto(
            name = "Copy method via binary file and property",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/insert/{count}")
    fun insertViaInsert(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByInsert(count)
        }
        return ResponseDto(
            name = "Insert method",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/insert-with-transaction/{count}")
    fun insertViaInsertWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByInsertWithTransaction(count)
        }
        return ResponseDto(
            name = "Insert method with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/update-with-transaction/{count}")
    fun updateWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.updateWithTransaction(count)
        }
        return ResponseDto(
            name = "Update method with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/insert-by-property-with-transaction/{count}")
    fun insertViaInsertAndPropertyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByInsertAndPropertyWithTransaction(count)
        }
        return ResponseDto(
            name = "Insert method by property with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/update-by-property-with-transaction/{count}")
    fun updateViaInsertAndPropertyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.updateByPropertyWithTransaction(count)
        }
        return ResponseDto(
            name = "Update method by property with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/update-only-one-field-by-property-with-transaction/{count}")
    fun updateOnlyOneFieldViaPropertyWithTransaction(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.updateOnlyOneFieldByPropertyWithTransaction(count)
        }
        return ResponseDto(
            name = "Update only one field by property with transaction",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/insert-with-drop-index/{count}")
    fun insertViaInsertWithDropIndex(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByInsertWithDropIndex(count)
        }
        return ResponseDto(
            name = "Insert method with drop index",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/spring/{count}")
    fun insertViaSpring(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveBySpring(count)
        }
        return ResponseDto(
            name = "Save via spring",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/spring-save-all/{count}")
    fun insertViaSaveAllSpring(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveAllBySpring(count)
        }
        return ResponseDto(
            name = "Save all via Spring",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/spring-with-manual-batching/{count}")
    fun insertViaSpringWithManualBatching(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveBySpringWithManualBathing(count)
        }
        return ResponseDto(
            name = "Save via Spring with manual batching",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/spring-with-copy/{count}")
    fun insertViaSpringWithCopy(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.saveByCopyViaSpring(count)
        }
        return ResponseDto(
            name = "Save via spring with copy method",
            count = count,
            time = getTimeString(time)
        )
    }

    @PostMapping("/spring-update/{count}")
    fun updateViaSpring(@PathVariable count: Int): ResponseDto {
        val time = measureTimeMillis {
            service.updateBySpring(count)
        }
        return ResponseDto(
            name = "Update via spring",
            count = count,
            time = getTimeString(time)
        )
    }

    private fun getTimeString(time: Long):String {
        val min = (time / 1000) / 60
        val sec = (time / 1000) % 60
        val ms = time - min*1000*60 - sec*1000
        return "$min min, $sec sec $ms ms"
    }
}