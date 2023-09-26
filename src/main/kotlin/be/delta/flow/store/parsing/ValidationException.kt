package be.delta.flow.store.parsing

data class ValidationException(val errors: List<ValidationError>) :
    Exception("Validation contains errors: \n\t${errors.joinToString("\n\t")}")