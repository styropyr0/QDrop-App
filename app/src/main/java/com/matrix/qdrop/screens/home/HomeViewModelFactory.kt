import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.matrix.qdrop.Repository
import com.matrix.qdrop.screens.home.HomeViewModel

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
