package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import services.CTFService
import services.GroupService
import utilities.Console
import utilities.ExportFileManagement
import java.sql.SQLException

@Composable
fun CTFManagerApp(groupService: GroupService, ctfService: CTFService) {
    var inputText by remember { mutableStateOf(TextFieldValue(text = "")) }
    var displayInfo by remember { mutableStateOf("") }
    val groupList = remember { mutableStateListOf<String>() }

    // Carga la información inicial de todos los grupos
    LaunchedEffect(Unit) {
        groupList.clear()
        val groups = groupService.getAllGroups()
        groups.forEach { group ->
            groupList.add("ID: ${group.groupid}, Desc: ${group.groupdesc}, Mejor CTF: ${group.bestpostctfid ?: "N/A"}")
        }
    }
    // Imagen de background de la ventana.
    val backgroundImage: Painter = painterResource(resourcePath = "background.png")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Introduce el ID del grupo.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Lógica para mostrar información
                        val id = inputText.text.toIntOrNull()
                        displayInfo = if (id != null) {
                            val group = groupService.getGroupByID(id)
                            if (group != null) {
                                "ID: ${group.groupid}, Desc: ${group.groupdesc}, Mejor CTF: ${group.bestpostctfid ?: "N/A"}"
                            } else {
                                "Grupo no encontrado."
                            }
                        } else {
                            "ID no válido."
                        }
                        inputText = TextFieldValue("") // Limpiar el campo
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mostrar")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // Lógica para exportar clasificación
                        val exportPath = "src/main/resources/clasificacion_ctfs.txt"
                        val exportManager = ExportFileManagement(console = Console(), filePath = exportPath)
                        exportManager.clearFile()
                        exportCTFClassification(ctfService, groupService, exportPath)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exportar")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayInfo,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            ) {
                LazyColumn {
                    items(groupList) { groupInfo ->
                        Text(
                            text = groupInfo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Función para exportar la clasificación de CTFs.
 * @param ctfService El servicio de CTF para acceder a los datos de CTF.
 * @param groupService El servicio de grupo para acceder a los datos de los grupos.
 * @param filename El nombre del archivo donde se exportará la clasificación.
 */
fun exportCTFClassification(ctfService: CTFService, groupService: GroupService, filename: String) {
    val console = Console()
    val exportManager = ExportFileManagement(console, filename)
    try {
        console.showInfo("Exportando clasificación a $filename")
        val allCTFs = ctfService.getAllCTFs()
        val groupedCTFs = allCTFs.groupBy { it.ctfId }

        exportManager.clearFile()

        groupedCTFs.forEach { (ctfId, ctfList) ->
            exportManager.writeToFile("CTF: $ctfId\n")
            ctfList.sortedByDescending { it.score }.forEachIndexed { index, ctf ->
                val groupName = groupService.getGroupByID(ctf.groupid)?.groupdesc ?: "Unknown"
                exportManager.writeToFile("${index + 1}. $groupName (${ctf.score} puntos)\n")
            }
            exportManager.writeToFile("\n")
        }
        console.showInfo("Clasificación exportada correctamente a $filename")
    } catch (e: SQLException) {
        console.showError("Error al exportar la clasificación: ${e.message}")
    } catch (e: Exception) {
        console.showError("Error inesperado al exportar la clasificación: ${e.message}")
    }
}
