import socket

# Create a client socket


# Connect to the server
import time

filename = 'OutGateDemo1.mp4'
import numpy as np
import cv2

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
clientSocket.connect(("127.0.0.1", 6847))
clientSocket.settimeout(3)
cap = cv2.VideoCapture(filename)

# print('open')
# i=0

while(cap.isOpened()):
  ret, frame = cap.read()
  image_bytes = cv2.imencode('.jpg', frame)[1].tobytes()
  size = len(image_bytes)
  print('send b ',hex(image_bytes[size-3]),hex(image_bytes[size-2]),hex(image_bytes[size-1]))

  clientSocket.sendall(image_bytes)
  # print('Sent')
  # returnMsg = clientSocket.recv(2048)
  # print('returnMsg:',returnMsg)
  # clientSocket.close()
  # clientSocket.shutdown()

  # time.sleep(1)
  # i+=1
  # break

  # gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
  # cv2.imshow('frame',gray)
  # if cv2.waitKey(1) & 0xFF == ord('q'):
  #   break
print('finish')
cap.release()
# cv2.destroyAllWindows()