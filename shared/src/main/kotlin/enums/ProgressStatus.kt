package enums

enum class ProgressStatus(val status: String) {
    IN_PROGRESS("В процессе"),
    COMPLETE("Выполнен"),
    OVERDUE("Просрочен"),
    CANCELED("Отменен")
}