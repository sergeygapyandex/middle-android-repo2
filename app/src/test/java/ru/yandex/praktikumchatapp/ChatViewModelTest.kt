import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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
        val testMessage = "MyTestMessage"
        assert(viewModel.messages.value.isEmpty()) { "Начальный список должен быть пустой" }
        val message = Message.MyMessage(testMessage)
        viewModel.sendMyMessage(message.text)
        val listMessage = viewModel.messages.value
        assert(listMessage.isNotEmpty()) { "Flow не заполнился" }
        assert(listMessage.size == 1) { "Размер списка должен быть 1" }
        assert(listMessage.contains(message)) { "Значение неправильное" }
        assert(listMessage.last() is Message.MyMessage) { "Последнее значение должно быть типа MyMessage" }
        assert((listMessage.last() as Message.MyMessage).text == testMessage) { "Текст не совпадает" }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        coroutineScope {
            val jobs = messagesToSend.map { message ->
                launch { viewModel.sendMyMessage(message.text) }
            }
            jobs.joinAll()
            val listMessage = viewModel.messages.value
            assert(listMessage.size == 100) { "Значений должно быть 100" }

            val expectedTexts = messagesToSend.map { it.text }.toSet()
            val actualText = listMessage.map { (it as Message.MyMessage).text }.toSet()
            assert(actualText == expectedTexts) { "Списки не совпадают" }
        }
    }
}