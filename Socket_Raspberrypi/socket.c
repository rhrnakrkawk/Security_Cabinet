#include "socket.h"
// pthread_mutex_t 타입의 전역 변수 g_mutex를 선언한다.
pthread_mutex_t g_mutex;

// getCommand 구조체를 인자로 받는 connection 함수를 정의한다.
void connection(getCommand *getcmd)
{
    // socket_connect 함수를 호출한다.
    socket_connect(getcmd);
}

// getCommand 구조체를 인자로 받는 socket_connect 함수를 정의한다.
void socket_connect(getCommand *getcmd)
{
    int sockfd;                     // 소켓 파일 디스크립터를 저장할 변수를 선언한다.
    struct sockaddr_in server_addr; // 서버 주소 정보를 저장할 구조체를 선언한다.
    struct sockaddr_in board_addr;  // 클라이언트 주소 정보를 저장할 구조체를 선언한다.
    uint8_t sin_size;               // 주소 정보 구조체의 크기를 저장할 변수를 선언한다.
    int val = 1;                    // 소켓 옵션을 설정할 때 사용할 변수를 선언하고 1로 초기화한다.

    // pthread_mutex_t 타입의 g_mutex 변수를 초기화한다.
    pthread_mutex_init(&g_mutex, NULL);

    // 소켓을 생성하고 소켓 파일 디스크립터를 sockfd 변수에 저장한다.
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) // 소켓 생성에 실패한 경우
    {
        perror("Client error!"); // 에러 메시지를 출력한다.
        exit(EXIT_FAILURE);      // 프로그램을 종료한다.
    }

    // 서버 주소 정보 구조체를 초기화한다.
    server_addr.sin_family = AF_INET;         // IPv4 주소 체계를 사용한다.
    server_addr.sin_port = htons(SERV_PORT);  // 서버 포트 번호를 저장한다.
    server_addr.sin_addr.s_addr = INADDR_ANY; // 모든 인터페이스에서 접속을 허용한다.
    memset(&(server_addr.sin_zero), 0, 8);    // 나머지 부분을 0으로 초기화한다.

    // 소켓 옵션을 설정한다.
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *)&val, sizeof val) < 0)
    {
        perror("setsockopt"); // 에러 메시지를 출력한다.
        close(sockfd);        // 소켓을 닫는다.
    }

    // 소켓에 주소 정보를 바인딩한다.
    if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1)
    {
        perror("Server-bind() error!"); // 에러 메시지를 출력한다.
        exit(EXIT_FAILURE);             // 프로그램을 종료한다.
    }

    // 소켓을 대기 상태로 전환한다.
    if (listen(sockfd, BACKLOG) == -1)
    {
        perror("listen() error!"); // 에러 메시지를 출력한다.
        exit(EXIT_FAILURE);        // 프로그램을 종료한다.
    }

    // 클라이언트의 접속을 대기한다.
    sin_size = sizeof(struct sockaddr_in);
    getcmd->connection = accept(sockfd, (struct sockaddr *)&board_addr, &sin_size);

    printf("Connected\n"); // 접속이 성공적으로 이루어졌음을 출력한다.

    // 새로운 스레드를 생성하고 client_command 함수를 실행한다.
    pthread_t t_thread;
    pthread_create(&t_thread, NULL, client_command, getcmd);
}

// getCommand 구조체를 인자로 받는 client_command 함수를 정의한다.
void client_command(getCommand *getcmd)
{
    char get_command[BUFFSIZE]; // 클라이언트로부터 받은 명령어를 저장할 배열을 선언한다.
    while (TRUE)                // 무한 루프를 실행한다.
    {
        recv(getcmd->connection, get_command, sizeof(get_command), 0); // 클라이언트로부터 명령어를 받는다.
        if (strcmp(get_command, "\0") == 0)                            // 클라이언트와의 연결이 끊어진 경우
        {
            printf("FD[%d]의 연결이 끊어졌습니다.\n", getcmd->connection); // 연결이 끊어졌음을 출력한다.
            close(getcmd->connection);                                     // 소켓을 닫는다.
            pthread_exit(EXIT_SUCCESS);                                    // 스레드를 종료한다.
        }
        pthread_mutex_lock(&g_mutex);   
        getcmd->command = &get_command; // 명령어를 getcmd 구조체의 command 멤버에 저장한다.
        pthread_mutex_unlock(&g_mutex);
    }
}

// 문자열과 getCommand 구조체를 인자로 받는 response 함수를 정의한다.
void response(char *msg, getCommand *getcmd)
{
    pthread_mutex_lock(&g_mutex);                   
    write(getcmd->connection, msg, strlen(msg) + 1); // 클라이언트에게 응답을 보낸다.
    pthread_mutex_unlock(&g_mutex);                  
    getcmd->command = NULL;                          // getcmd 구조체의 command 멤버를 NULL로 초기화한다.
}

// getCommand 구조체를 인자로 받는 safety 함수를 정의한다.
uint8_t safety(getCommand *getcmd)
{
    return (getcmd->connection != 0 && getcmd->command != NULL) ? 1 : 0; // getcmd 구조체의 connection 멤버와 command 멤버가 모두 유효한 경우 1을 반환하고, 그렇지 않은 경우 0을 반환한다.
}
