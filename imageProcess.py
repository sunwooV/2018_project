# 점자 테두리 따기
import tcpServer
import executer
import time
import numpy as np
import cv2
import os
import matplotlib.pyplot as plt 
from PIL import Image
from multiprocessing import Queue


num = 48

def order_points(pts):
    # initialzie a list of coordinates that will be ordered
    # such that the first entry in the list is the top-left,
    # the second entry is the top-right, the third is the
    # bottom-right, and the fourth is the bottom-left
    rect = np.zeros((4, 2), dtype = "float32")

    # the top-left point will have the smallest sum, whereas
    # the bottom-right point will have the largest sum
    s = pts.sum(axis = 1)
    rect[0] = pts[np.argmin(s)]
    rect[2] = pts[np.argmax(s)]

    # now, compute the difference between the points, the
    # top-right point will have the smallest difference,
    # whereas the bottom-left will have the largest difference
    diff = np.diff(pts, axis = 1)
    rect[1] = pts[np.argmin(diff)]
    rect[3] = pts[np.argmax(diff)]

    # return the ordered coordinates
    return rect

def auto_scan_image(image):
    # load the image and compute the ratio of the old height
    # to the new height, clone it, and resize it
    # document.jpg ~ docuemnt7.jpg
    #image = cv2.imread('img/testimg/test' +str(num)+ '.jpeg')
    orig = image.copy()
    r = 800.0 / image.shape[0]
    dim = (int(image.shape[1] * r), 800)
    image = cv2.resize(image, dim, interpolation = cv2.INTER_AREA)

    # convert the image to grayscale, blur it, and find edges
    # in the image
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (1, 3), 0)
    edged = cv2.Canny(gray, 25, 50)

    # show the original image and the edge detected image
    print ("STEP 1: Edge Detection")
    plt.imshow(image)
    plt.show()
    #cv2.imshow("Image", image)
    #cv2.imshow("Edged", edged)
    
    #cv2.waitKey(0)
    #cv2.destroyAllWindows()
    #cv2.waitKey(1)

    # find the contours in the edged image, keeping only the
    # largest ones, and initialize the screen contour
    (_, cnts, _) = cv2.findContours(edged.copy(), cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
    cnts = sorted(cnts, key = cv2.contourArea, reverse = True)[:5]

    # loop over the contours
    for c in cnts:
        # approximate the contour
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.01 * peri, True)

        # if our approximated contour has four points, then we
        # can assume that we have found our screen
        if len(approx) == 4:
            screenCnt = approx
            break

    # show the contour (outline) of the piece of paper
    
    
    print ("STEP 2: Find contours of paper")
    cv2.drawContours(image, [screenCnt], -1, (0, 255, 0), 2)
    plt.imshow(image)
    plt.show()
    #cv2.imshow("Outline", image)
    
    #cv2.waitKey(0)
    #cv2.destroyAllWindows()
    #cv2.waitKey(1)

    # apply the four point transform to obtain a top-down
    # view of the original image
    rect = order_points(screenCnt.reshape(4, 2) / r)
    (topLeft, topRight, bottomRight, bottomLeft) = rect
    
    w1 = abs(bottomRight[0] - bottomLeft[0])
    w2 = abs(topRight[0] - topLeft[0])
    h1 = abs(topRight[1] - bottomRight[1])
    h2 = abs(topLeft[1] - bottomLeft[1])
    maxWidth = max([w1, w2])
    maxHeight = max([h1, h2])
    
    dst = np.float32([[0,0], [maxWidth-1,0], 
                      [maxWidth-1,maxHeight-1], [0,maxHeight-1]])
    
    M = cv2.getPerspectiveTransform(rect, dst)
    warped = cv2.warpPerspective(orig, M, (maxWidth, maxHeight))

    # show the original and scanned images
    print ("STEP 3: Apply perspective transform")
    plt.imshow(warped)
    plt.show()
    
    img1 = cv2.resize(warped, (700, 230), interpolation= cv2.INTER_AREA)
    cv2.imwrite("img/resultimg/res" + str(num) + ".jpg", img1) 

    #cv2.imshow("Warped", warped)
    
    
    #cv2.waitKey(0)
    #cv2.destroyAllWindows()
    #cv2.waitKey(1)
    
    # convert the warped image to grayscale, then threshold it
    # to give it that 'black and white' paper effect
    warped = cv2.cvtColor(warped, cv2.COLOR_BGR2GRAY)
    warped = cv2.adaptiveThreshold(warped, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 21, 10)

    # show the original and scanned images
    print ("STEP 4: Apply Adaptive Threshold")
    #cv2.imshow("Original", orig)
    #cv2.imshow("Scanned", warped)
    #cv2.imwrite('scannedImage.png', warped)
    
    #cv2.waitKey(0)
    #cv2.destroyAllWindows()
    #cv2.waitKey(1)
    
    return screenCnt

            
            
if __name__ == '__main__':    
    image = None
    txtfile = None
    line = ""
    s = "/Users/woobin/Desktop/매트랩__.txt"
    
    while True:
        if image is None :
            image = cv2.imread('img/testimg/test' +str(num)+ '.jpeg')            
        else :
            auto_scan_image(image)
            break
        #plt.imshow(image)
        #plt.show()
        
    while True :
        if os.path.isfile(s) :
            txtfile = open(s,'r')
            break
        else :
            continue
            
            
    if txtfile is not None :
       # make public queue
        commandQueue = Queue()
        # init module
        andRaspTCP = tcpServer.TCPServer(commandQueue, "", 7784)
        andRaspTCP.start()

        # set module to executer
        commandExecuter = executer.Executer(andRaspTCP)
        f = txtfile
        while True:
            aa = f.readline()
            if not aa: break
            line = aa
            print(line)
        f.close()
        while True:
            try:
                command = commandQueue.get()
                commandExecuter.startCommand(command, line)
                print(line)
            except:
                print("error")
                pass