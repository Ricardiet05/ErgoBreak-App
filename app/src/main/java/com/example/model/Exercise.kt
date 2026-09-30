package com.example.model

enum class TensionZone(
    val id: String,
    val titleEn: String,
    val titleEs: String,
    val descriptionEn: String,
    val descriptionEs: String,
    val isPro: Boolean
) {
    NECK(
        id = "neck",
        titleEn = "Neck",
        titleEs = "Cuello",
        descriptionEn = "Relieve cervical tension & stiffness",
        descriptionEs = "Alivia la tensión cervical y rigidez",
        isPro = false
    ),
    SHOULDERS(
        id = "shoulders",
        titleEn = "Shoulders",
        titleEs = "Hombros",
        descriptionEn = "Release upper trap & shoulder tightness",
        descriptionEs = "Libera la carga en hombros y trapecios",
        isPro = false
    ),
    BACK(
        id = "back",
        titleEn = "Back",
        titleEs = "Espalda",
        descriptionEn = "Improve spinal mobility & posture",
        descriptionEs = "Mejora la movilidad espinal y postura",
        isPro = true
    ),
    WRISTS_AND_HANDS(
        id = "wrists_hands",
        titleEn = "Wrists & Hands",
        titleEs = "Muñecas y Manos",
        descriptionEn = "Ease keyboard fatigue & improve circulation",
        descriptionEs = "Alivia fatiga de teclado y favorece circulación",
        isPro = true
    ),
    FULL_BODY(
        id = "full_body",
        titleEn = "Full Body",
        titleEs = "Cuerpo Completo",
        descriptionEn = "Complete desk reset from head to legs",
        descriptionEs = "Reinicio completo de cabeza a piernas",
        isPro = true
    );

    fun getTitle(language: Language): String = if (language == Language.ES) titleEs else titleEn
    fun getDescription(language: Language): String = if (language == Language.ES) descriptionEs else descriptionEn
}

enum class VisualPostureType {
    NECK_TILT,
    NECK_ROTATION,
    CHIN_TUCK,
    SHOULDER_LIFT,
    SHOULDER_ROLLS,
    CHEST_OPENER,
    ARM_ACROSS,
    TORSO_ROTATION,
    BACK_EXTENSION,
    WRIST_STRETCH,
    WRIST_CIRCLES,
    KNEE_LIFTS,
    LEG_EXTENSIONS
}

data class Exercise(
    val id: String,
    val code: String,
    val nameEn: String,
    val nameEs: String,
    val movementEn: String,
    val movementEs: String,
    val primaryZone: TensionZone,
    val durationSeconds: Int = 30,
    val videoFileName: String,
    val visualType: VisualPostureType
) {
    fun getName(language: Language): String = if (language == Language.ES) nameEs else nameEn
    fun getMovement(language: Language): String = if (language == Language.ES) movementEs else movementEn
}

object ExerciseRepository {
    val EX01 = Exercise(
        id = "ex01",
        code = "EX01",
        nameEn = "Neck Side Tilt",
        nameEs = "Inclinación lateral de cuello",
        movementEn = "Gently tilt head toward one shoulder, then toward the other side.",
        movementEs = "Inclinar suavemente la cabeza hacia un hombro y luego hacia el otro lado.",
        primaryZone = TensionZone.NECK,
        durationSeconds = 30,
        videoFileName = "ex01_neck_side_tilt",
        visualType = VisualPostureType.NECK_TILT
    )

    val EX02 = Exercise(
        id = "ex02",
        code = "EX02",
        nameEn = "Neck Rotation",
        nameEs = "Rotación de cuello",
        movementEn = "Slowly turn head to the left, and then to the right.",
        movementEs = "Girar lentamente la cabeza hacia la izquierda y luego hacia la derecha.",
        primaryZone = TensionZone.NECK,
        durationSeconds = 30,
        videoFileName = "ex02_neck_rotation",
        visualType = VisualPostureType.NECK_ROTATION
    )

    val EX03 = Exercise(
        id = "ex03",
        code = "EX03",
        nameEn = "Chin Tuck",
        nameEs = "Retracción de mentón",
        movementEn = "Gently draw chin backward, like making a double chin, keeping head level.",
        movementEs = "Llevar suavemente el mentón hacia atrás, como haciendo una doble barbilla, manteniendo la cabeza nivelada.",
        primaryZone = TensionZone.NECK,
        durationSeconds = 30,
        videoFileName = "ex03_chin_tuck",
        visualType = VisualPostureType.CHIN_TUCK
    )

    val EX04 = Exercise(
        id = "ex04",
        code = "EX04",
        nameEn = "Shoulder Lift and Relax",
        nameEs = "Elevación y relajación de hombros",
        movementEn = "Elevate both shoulders toward ears, then relax and lower them.",
        movementEs = "Elevar ambos hombros hacia las orejas y luego relajarlos y bajarlos.",
        primaryZone = TensionZone.SHOULDERS,
        durationSeconds = 30,
        videoFileName = "ex04_shoulder_lift_and_relax",
        visualType = VisualPostureType.SHOULDER_LIFT
    )

    val EX05 = Exercise(
        id = "ex05",
        code = "EX05",
        nameEn = "Shoulder Rolls",
        nameEs = "Círculos de hombros",
        movementEn = "Make gentle circles with shoulders backward, then forward.",
        movementEs = "Hacer círculos suaves con los hombros hacia atrás y luego hacia adelante.",
        primaryZone = TensionZone.SHOULDERS,
        durationSeconds = 30,
        videoFileName = "ex05_shoulder_rolls",
        visualType = VisualPostureType.SHOULDER_ROLLS
    )

    val EX06 = Exercise(
        id = "ex06",
        code = "EX06",
        nameEn = "Chest Opener",
        nameEs = "Apertura de pecho",
        movementEn = "Bring arms gently backward to open chest and maintain upright posture.",
        movementEs = "Llevar los brazos suavemente hacia atrás para abrir el pecho y mantener una postura erguida.",
        primaryZone = TensionZone.SHOULDERS,
        durationSeconds = 30,
        videoFileName = "ex06_chest_opener",
        visualType = VisualPostureType.CHEST_OPENER
    )

    val EX07 = Exercise(
        id = "ex07",
        code = "EX07",
        nameEn = "Arm Across Chest Stretch",
        nameEs = "Estiramiento de brazo cruzado",
        movementEn = "Extend one arm in front and cross gently over chest. Alternate arms.",
        movementEs = "Extender un brazo frente al cuerpo y cruzarlo suavemente sobre el pecho. Alternar los brazos.",
        primaryZone = TensionZone.SHOULDERS,
        durationSeconds = 30,
        videoFileName = "ex07_arm_across_chest_stretch",
        visualType = VisualPostureType.ARM_ACROSS
    )

    val EX08 = Exercise(
        id = "ex08",
        code = "EX08",
        nameEn = "Seated Torso Rotation",
        nameEs = "Rotación de torso sentado",
        movementEn = "Seated, gently rotate torso to one side, then to the other.",
        movementEs = "Sentado, girar suavemente el torso hacia un lado y luego hacia el otro.",
        primaryZone = TensionZone.BACK,
        durationSeconds = 30,
        videoFileName = "ex08_seated_torso_rotation",
        visualType = VisualPostureType.TORSO_ROTATION
    )

    val EX09 = Exercise(
        id = "ex09",
        code = "EX09",
        nameEn = "Seated Back Extension",
        nameEs = "Extensión de espalda sentado",
        movementEn = "Seated, straighten back and gently lift chest for a mild back extension.",
        movementEs = "Sentado, enderezar la espalda y elevar suavemente el pecho para realizar una extensión ligera de la espalda.",
        primaryZone = TensionZone.BACK,
        durationSeconds = 30,
        videoFileName = "ex09_seated_back_extension",
        visualType = VisualPostureType.BACK_EXTENSION
    )

    val EX10 = Exercise(
        id = "ex10",
        code = "EX10",
        nameEn = "Wrist Stretch",
        nameEs = "Estiramiento de muñeca",
        movementEn = "Extend one arm and perform a gentle wrist & forearm stretch. Alternate hands.",
        movementEs = "Extender un brazo y realizar un estiramiento suave de la muñeca y antebrazo. Alternar las manos.",
        primaryZone = TensionZone.WRISTS_AND_HANDS,
        durationSeconds = 30,
        videoFileName = "ex10_wrist_stretch",
        visualType = VisualPostureType.WRIST_STRETCH
    )

    val EX11 = Exercise(
        id = "ex11",
        code = "EX11",
        nameEn = "Wrist Circles",
        nameEs = "Círculos de muñecas",
        movementEn = "Perform gentle, smooth circles with your wrists.",
        movementEs = "Realizar círculos suaves con las muñecas.",
        primaryZone = TensionZone.WRISTS_AND_HANDS,
        durationSeconds = 30,
        videoFileName = "ex11_wrist_circles",
        visualType = VisualPostureType.WRIST_CIRCLES
    )

    val EX12 = Exercise(
        id = "ex12",
        code = "EX12",
        nameEn = "Alternating Knee Lifts",
        nameEs = "Elevación alterna de rodillas",
        movementEn = "Seated, lift one knee up, then the other in alternation.",
        movementEs = "Sentado, levantar una rodilla y luego la otra de forma alternada.",
        primaryZone = TensionZone.FULL_BODY,
        durationSeconds = 30,
        videoFileName = "ex12_alternating_knee_lifts",
        visualType = VisualPostureType.KNEE_LIFTS
    )

    val EX13 = Exercise(
        id = "ex13",
        code = "EX13",
        nameEn = "Leg Extensions",
        nameEs = "Extensiones de piernas",
        movementEn = "Seated, extend one leg forward, lower it, and alternate with the other leg.",
        movementEs = "Sentado, extender una pierna hacia adelante, bajarla y alternar con la otra pierna.",
        primaryZone = TensionZone.FULL_BODY,
        durationSeconds = 30,
        videoFileName = "ex13_leg_extensions",
        visualType = VisualPostureType.LEG_EXTENSIONS
    )

    val ALL_EXERCISES = listOf(
        EX01, EX02, EX03, EX04, EX05, EX06,
        EX07, EX08, EX09, EX10, EX11, EX12, EX13
    )

    fun getExercisesForZone(zone: TensionZone): List<Exercise> {
        return when (zone) {
            TensionZone.NECK -> listOf(EX01, EX02, EX03)
            TensionZone.SHOULDERS -> listOf(EX04, EX05, EX06, EX07)
            TensionZone.BACK -> listOf(EX06, EX08, EX09)
            TensionZone.WRISTS_AND_HANDS -> listOf(EX10, EX11)
            TensionZone.FULL_BODY -> listOf(EX01, EX04, EX06, EX08, EX10, EX12, EX13)
        }
    }
}
