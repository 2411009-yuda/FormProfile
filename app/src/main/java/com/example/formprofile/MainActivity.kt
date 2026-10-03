package com.example.formprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ===== Warna (diambil dari desain HTML) =====
val BrandBlue = Color(0xFF0E49B5)
val BrandDarkBlue = Color(0xFF0A3482)
val BrandRed = Color(0xFFE11D48)
val BrandGold = Color(0xFFF59E0B)
val Slate900 = Color(0xFF0F172A)
val Slate500 = Color(0xFF64748B)
val Slate700 = Color(0xFF334155)

data class Mahasiswa(val nama: String, val nim: String, val prodi: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProfilScreen()
            }
        }
    }
}

// ================= PARENT (stateful) =================
@Composable
fun ProfilScreen() {
    // STATE
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var tersimpan by remember { mutableStateOf<Mahasiswa?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Validasi dihitung dari state
    val namaValid = nama.isNotBlank()
    val nimValid = nim.isNotBlank() && nim.all { it.isDigit() }
    val prodiValid = prodi.isNotBlank()
    val semuaValid = namaValid && nimValid && prodiValid

    Scaffold(
        containerColor = Color.White,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data -> ToastSukses(data) }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            BrandHeader()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Form Profil Mahasiswa", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Slate900)
                }

                RingkasanMahasiswa(tersimpan)

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InputField(
                        label = "Nama Lengkap",
                        placeholder = "Masukkan nama lengkap",
                        icon = Icons.Default.Person,
                        value = nama,
                        onValueChange = { nama = it },   // EVENT -> ubah STATE
                        isError = nama.isNotEmpty() && !namaValid,
                        errorMessage = "Nama tidak boleh kosong"
                    )
                    InputField(
                        label = "NIM (Nomor Induk Mahasiswa)",
                        placeholder = "Contoh: 2411009",
                        icon = Icons.Default.AccountBox,
                        value = nim,
                        onValueChange = { nim = it },
                        isError = nim.isNotEmpty() && !nimValid,
                        errorMessage = "NIM harus berupa angka"
                    )
                    InputField(
                        label = "Program Studi",
                        placeholder = "Contoh: S1 Informatika",
                        icon = Icons.Default.Star,
                        value = prodi,
                        onValueChange = { prodi = it },
                        isError = prodi.isNotEmpty() && !prodiValid,
                        errorMessage = "Program studi tidak boleh kosong"
                    )
                }

                TombolSimpan(enabled = semuaValid) {      // EVENT klik
                    tersimpan = Mahasiswa(nama.trim(), nim.trim(), prodi.trim()) // ubah STATE
                    scope.launch {
                        snackbarHostState.showSnackbar("Data profil berhasil disimpan")
                    }
                }

                KartuProfil(tersimpan)
            }
        }
    }
}

// ================= CHILD (stateless) =================

@Composable
fun BrandHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFFEFF6FF), Color.White)))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_mulia),
            contentDescription = "Logo Universitas Mulia",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(6.dp)
        )
        Column {
            Text("SISTEM AKADEMIK TERPADU", fontSize = 10.sp,
                fontWeight = FontWeight.Bold, color = BrandRed, letterSpacing = 1.sp)
            Text("UNIVERSITAS MULIA", fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold, color = BrandDarkBlue)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(BrandGold))
                Spacer(Modifier.width(6.dp))
                Text("Inovatif • Mandiri • Humanis", fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, color = Color(0xFFD97706))
            }
        }
    }
    HorizontalDivider(color = Color(0xFFF1F5F9))
}

@Composable
fun RingkasanMahasiswa(data: Mahasiswa?) {
    // Inisial dihitung dari state, ikut berubah saat recomposition
    val inisial = data?.nama
        ?.split(" ")?.filter { it.isNotBlank() }?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?: "?"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFEFF6FF), Color(0xFFF8FAFC))))
            .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(BrandBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(inisial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(data?.nama ?: "Belum ada data", fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = Slate900)
                if (data != null) {
                    Text(
                        "Aktif", fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF065F46),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFD1FAE5))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text("NIM: ${data?.nim ?: "-"}", fontSize = 12.sp,
                color = Slate500, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun InputField(
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
            Text(" *", fontSize = 12.sp, color = BrandRed)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = BrandBlue.copy(alpha = 0.7f)) },
            isError = isError,
            supportingText = { if (isError) Text(errorMessage) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                errorBorderColor = BrandRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TombolSimpan(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(BrandBlue, Color(0xFF4338CA)))
                    else SolidColor(Color(0xFFCBD5E1)),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(16.dp))
                Text("Simpan Perubahan", color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun KartuProfil(data: Mahasiswa?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Slate900, BrandDarkBlue, Color(0xFF172554))))
    ) {
        // Garis aksen tiga warna
        Row(Modifier.fillMaxWidth().height(6.dp)) {
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFDC2626)))
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFBBF24)))
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF3B82F6)))
        }

        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("KARTU DIGITAL • SEMESTER GENAP", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24), letterSpacing = 1.sp)
                    Text("Profil Mahasiswa", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = Color.White)
                }
                // Badge muncul hanya jika data sudah tersimpan
                if (data != null) {
                    Text(
                        "✓ Terdaftar", fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6EE7B7),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            HorizontalDivider(color = Color(0xFF60A5FA).copy(alpha = 0.2f))

            BarisProfil("Nama", data?.nama ?: "-", Color.White)
            BarisProfil("NIM", data?.nim ?: "-", Color(0xFFFCD34D), mono = true)
            BarisProfil("Program Studi", data?.prodi ?: "-", Color.White)
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            BarisProfil("Perguruan Tinggi", "Universitas Mulia", Color(0xFFFDA4AF))
        }
    }
}

@Composable
fun BarisProfil(label: String, nilai: String, warnaNilai: Color, mono: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFFBFDBFE).copy(alpha = 0.8f))
        Text(
            nilai, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = warnaNilai,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@Composable
fun ToastSukses(data: SnackbarData) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null,
                tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(data.visuals.message, color = Color(0xFFE2E8F0),
            fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        IconButton(onClick = { data.dismiss() }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Tutup",
                tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
        }
    }
}