import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        assert(viewModel.messages.value.isEmpty()) { "Начальный список должен быть пустой" }
        val message = Message.MyMessage("MyTestMessage")
        viewModel.sendMyMessage(message.text)
        val listMessage = viewModel.messages.value
        assert(listMessage.isNotEmpty()) { "Flow не заполнился" }
        assert(listMessage.size == 1) { "Размер списка должен быть 1" }
        assert(listMessage.contains(message)) { "Значение неправильное" }
        assert(listMessage.last() is Message.MyMessage) { "Последнее значение должно быть типа MyMessage" }
        assert((listMessage.last() as Message.MyMessage).text == "MyTestMessage") { "Текст не совпадает" }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

    }
}