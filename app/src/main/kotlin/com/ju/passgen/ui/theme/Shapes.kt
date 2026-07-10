package com.ju.passgen.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Basado exactamente en las --r-* vars del CSS web
// --r-sm: 8px  --r-md: 12px  --r-lg: 16px  --r-xl: 20px  --r-2xl: 24px
val JUShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // --r-sm
    small      = RoundedCornerShape(12.dp),  // --r-md
    medium     = RoundedCornerShape(16.dp),  // --r-lg
    large      = RoundedCornerShape(20.dp),  // --r-xl
    extraLarge = RoundedCornerShape(24.dp),  // --r-2xl
)
